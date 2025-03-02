
#include "renumeration_des_sections.h"



Symtab_nom_section* renumertion_des_sections( Elf32_Ehdr* en_tete, Elf32_Shdr* elf_section,  Elf32_Shdr** non_rel_sections , 
    SymbolTable *symtab ,char *section_names  ) {

    // Allocation de mémoire pour un objet de type Symtab_nom_section
    // Ce struct contiendra la table des symboles corrigée et la table des indices de sections.   
    Symtab_nom_section *symtab_nom_section = malloc(sizeof(Symtab_nom_section));

    // Compteur du nombre des sections après la suppression des sections REL
    int number_of_sections = 0;
    int j=-1, z=-1;

    // Structure contenant la table des sections et les noms des sections
    SectionHeaderTable section_table = {elf_section, (char*) section_names, en_tete->e_shnum};

    // Parcourir les sections pour détecter celles qui ne sont pas de type REL
    for (size_t i = 0; i < en_tete->e_shnum; i++) {
        Elf32_Shdr* section = &elf_section[i];

        // Vérifier si la section n'est pas de type SHT_REL ou SHT_REL

        if (section->sh_type != SHT_REL ){
            // Si la section est une table des symboles (SHT_SYMTAB), on garde son indice
            if (section->sh_type == SHT_SYMTAB)
            {
                j=number_of_sections;
            }
            // Si la section est une table de chaînes de caractères ".strtab", on garde son indice
            if (strcmp(&section_names[section->sh_name], ".strtab") == 0)
            {
                z=number_of_sections;
            }
            if (section->sh_flags & 0x2){
                section->sh_addr=section->sh_offset;
            }
            // Ajout de la section dans le tableau des sections non-relocation
            non_rel_sections[number_of_sections] = section;
            number_of_sections++;
        }
    }
    // On met à jour le lien de la table des symboles pour qu'elle fasse référence à la section ".strtab"
    non_rel_sections[j]->sh_link=z;


    // Modifier la position de la section de string table ".shstrtab"
    int shstrtab_index = -1;

    for (int i = 0; i < number_of_sections; i++) {
        if (strcmp(&section_names[non_rel_sections[i]->sh_name], ".shstrtab") == 0) {
            shstrtab_index = i;
            break;
        }
    }

    // Création d'une table d'indices de sections à partir des sections non-relocation
    nom_indice_section_table *table = create_nom_indice_section_table(non_rel_sections, *en_tete, section_names, elf_section, number_of_sections);
    
    // Mise à jour du nombre de sections dans l'en-tête ELF
    en_tete->e_shnum = number_of_sections;

    if (shstrtab_index == -1) {
        fprintf(stderr, "Erreur : Section .shstrtab non trouvée.\n");
        exit(EXIT_FAILURE);
    }

    // Mise à jour de l'indice de la section ".shstrtab" dans l'en-tête ELF
    en_tete->e_shstrndx = shstrtab_index;

    // Correction de la table des symboles en fonction de la nouvelle organisation des sections
    SymbolTable  *new_symtab = corriger_symbole(symtab, &section_table,  table);
    
    // Enregistrement de la nouvelle table des symboles et de la table d'indices dans la structure de retour
    symtab_nom_section->symtab = new_symtab;
    symtab_nom_section->table = table;

    printf("Sections renumérotées avec succès\n");
    return symtab_nom_section;
}
