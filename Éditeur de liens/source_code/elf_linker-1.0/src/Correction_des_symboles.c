#include "Correction_des_symboles.h"


// Fonction pour créer une table de correspondance entre les anciens et les nouveaux indices des sections
nom_indice_section_table* create_nom_indice_section_table(
    Elf32_Shdr** new_sections, 
    Elf32_Ehdr en_tete, 
    char* section_names, 
    Elf32_Shdr* elf_section, 
    int number_of_sections
) {
    // Allocation de mémoire pour la table des indices des sections
    // Chaque entrée de cette table va contenir un nom de section et ses indices
    nom_indice_section *new = malloc(sizeof(nom_indice_section) * number_of_sections);

    // Parcours de chaque section non-relocation pour créer la table des indices
    for (int i = 0; i < number_of_sections; i++) {

        // On assigne à chaque élément de la table "new" le nom de la section
        // L'adresse du nom de la section est déterminée par "sh_name", qui est un offset dans "section_names"
        new[i].names = &section_names[new_sections[i]->sh_name];

        // Le champ "nvindice" est l'indice de la section dans la nouvelle liste
        new[i].nvindice = i;

        // Recherche de l'indice original de la section dans la table des sections ELF
        // Ce processus permet de retrouver l'ancien indice de la section dans l'ensemble des sections ELF
        for (int j = 0; j < en_tete.e_shnum; j++) {
            
            // Comparaison des noms des sections dans "elf_section" avec ceux de "new"
            if (strcmp(&section_names[elf_section[j].sh_name], new[i].names) == 0) {
                new[i].ancienindice = j;
                break;
            }
        }
    }
    // Allocation de mémoire pour la table finale, qui contiendra toutes les entrées de "new"
    nom_indice_section_table *table = malloc(sizeof(nom_indice_section_table));

    // On assigne la taille de la table (le nombre de sections non-relocation)
    table->size = number_of_sections;

    // On assigne la table des indices des sections
    table->table = new;
    return table;
}


//*********************************************************************************************************************//
//*********************************************************************************************************************//



// Fonction pour corriger les symboles en fonction des nouveaux indices des sections
SymbolTable* corriger_symbole(SymbolTable *table_symbole, SectionHeaderTable *entete , nom_indice_section_table *table) {
    
    // Vérification des pointeurs pour s'assurer qu'ils sont valides
    if (!table_symbole || !entete) {
        fprintf(stderr, "Erreur : table des symboles ou table de relocation invalide.\n");
        return NULL;
    }

    // Boucle sur chaque symbole de la table des symboles pour effectuer les corrections
    for (size_t i = 0; i < table_symbole->size; i++) {
        Elf32_Sym *symbol = &table_symbole->symbols[i];
        Elf32_Word section_index = symbol->st_shndx;

        // Recherche de l'indice de la section corrigée dans la table des indices de sections
        for (int j = 0; j < table->size; j++) {
            // Si l'indice de la section du symbole correspond à l'ancien indice dans la table, on le corrige
            if (section_index == table->table[j].ancienindice) {
                symbol->st_shndx = table->table[j].nvindice;
                break;
            } 
        }
        // Vérification si l'indice de la section du symbole est valide (doit être inférieur à la taille des en-têtes de section)
        if (section_index >= entete->size) {
            fprintf(stderr, "Erreur : index de section invalide pour le symbole %zu.\n", i);
            return NULL;
        }

        // On récupère la section correspondante à l'indice du symbole
        Elf32_Shdr *section = &entete->headers[section_index];

        // Si le type de symbole est "SECTION" et que la section n'a pas le flag SHF_ALLOC (2ème bit), on continue
        // Cela permet de ne pas modifier des symboles de type SECTION qui ne sont pas dans des sections allouées
        if( strcmp(get_symbol_type(symbol->st_info), "SECTION") == 0 && !(section->sh_flags & 0x2) ) {
            continue;
        }

        // Mise à jour de l'adresse du symbole en fonction de l'offset de la section
        symbol->st_value += section->sh_addr;
    }
    printf("Table des symboles corrigée avec succès.\n");   
    return table_symbole;
}



