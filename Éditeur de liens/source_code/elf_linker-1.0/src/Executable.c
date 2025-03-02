#include "Executable.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

void section_alocabl(Elf32_Ehdr header, Elf32_Shdr *section_table) {
    // Allocation de mémoire pour un objet de type Executable
    Executable *executable = malloc(sizeof(Executable));
    if (executable == NULL) {
        perror("Erreur d'allocation de mémoire");
        exit(EXIT_FAILURE);
    }

    // Allocation de mémoire pour un tableau de sections allouables
    Elf32_Shdr *section_alocable = malloc(sizeof(Elf32_Shdr) * header.e_shnum);

    if (section_alocable == NULL) {
        perror("Erreur d'allocation de mémoire");
        free(executable);
        exit(EXIT_FAILURE);
    }

    int j = 0;
    Elf32_Word offset = 0;
    
    // Parcourir toutes les sections du fichier ELF
    for (int i = 0; i < header.e_shnum; i++) {
        // Vérifier si la section est allouable (flag SHF_ALLOC)
        if (section_table[i].sh_flags & SHF_ALLOC) { // Section allouable

            // Si c'est la première section allouable, mémoriser son offset
            if (j == 0) {
                offset = section_table[i].sh_offset; // Offset de la première section
            }

            // Copier la section allouable dans le tableau section_alocable
            section_alocable[j] = section_table[i];
            j++;
        }
    }
    // Si aucune section allouable n'a été trouvée, libérer la mémoire et sortir
    if (j == 0) { // Aucune section allouable trouvée
        free(section_alocable);
        free(executable);
        return ;
    }

    // Calculer la taille totale des sections allouables
    Elf32_Word last_section_offset = section_alocable[j - 1].sh_offset;
    Elf32_Word last_section_size = section_alocable[j - 1].sh_size;
    executable->size = (last_section_offset + last_section_size) - offset;

    // Assignation des sections allouables au champ 'section' de l'exécutable
    executable->section = section_alocable;
    printf("Taille totale des sections allouables : %d octets\n", executable->size);
}

//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//

Elf32_Addr get_adress_text(Elf32_Ehdr header, Elf32_Shdr *section_table) {
    for (int i = 0; i < header.e_shnum; i++) {
        if (section_table[i].sh_type == SHT_PROGBITS && // Section de type PROGBITS
            (section_table[i].sh_flags & SHF_ALLOC) && // Section allouable
            (section_table[i].sh_flags & SHF_EXECINSTR)) { // Section exécutable
            return section_table[i].sh_addr; // Adresse de la section .text
        }
    }
    return 0; // Si la section .text n'est pas trouvée
}

//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//

Elf32_Phdr *cree_segement(FILE *fichier, Elf32_Ehdr *header, Elf32_Addr base_address,char *section_names) {
    // Lire la table des sections
    Elf32_Shdr *section_table = read_sections_table(fichier, *header);
    char *shstrtab = section_names;
    if (section_table == NULL) {
        perror("Erreur lors de la lecture de la table des sections");
        exit(EXIT_FAILURE);
    }

    // Créer deux segments
    Elf32_Phdr *segments = malloc(2 * sizeof(Elf32_Phdr));
    if (segments == NULL) {
        perror("Erreur d'allocation de mémoire");
        exit(EXIT_FAILURE);
    }

    // Trouver les sections .text, .data et .bss
    Elf32_Shdr *text_section = NULL, *data_section = NULL, *bss_section = NULL;
    for (int i = 0; i < header->e_shnum; i++) {
        if (strcmp(&shstrtab[section_table[i].sh_name], ".text") == 0) {
            text_section = &section_table[i];
        } else if (strcmp(&shstrtab[section_table[i].sh_name], ".data") == 0) {
            data_section = &section_table[i];
        } else if (strcmp(&shstrtab[section_table[i].sh_name], ".bss") == 0) {
            bss_section = &section_table[i];
        }
    }

    if (text_section == NULL || data_section == NULL || bss_section == NULL) {
        fprintf(stderr, "Sections .text, .data ou .bss non trouvées\n");
        exit(EXIT_FAILURE);
    }

    // Segment 1 : Code (.text)
    segments[0].p_type = PT_LOAD;
    segments[0].p_offset = text_section->sh_offset; // Offset de .text
    segments[0].p_vaddr = base_address; // Adresse virtuelle de .text
    segments[0].p_paddr = base_address; // Adresse physique de .text
    segments[0].p_filesz = text_section->sh_size; // Taille de .text dans le fichier
    segments[0].p_memsz = text_section->sh_size; // Taille de .text en mémoire
    segments[0].p_flags = PF_R | PF_X; // Permissions : lecture et exécution
    segments[0].p_align = 0x1000; // Alignement

    // Segment 2 : Données (.data et .bss)
    segments[1].p_type = PT_LOAD;
    segments[1].p_offset = data_section->sh_offset; // Offset de .data
    segments[1].p_vaddr = base_address + 0x1000; // Adresse virtuelle de .data
    segments[1].p_paddr = base_address + 0x1000; // Adresse physique de .data
    segments[1].p_filesz = data_section->sh_size; // Taille de .data dans le fichier
    segments[1].p_memsz = data_section->sh_size + bss_section->sh_size; // Taille de .data + .bss en mémoire
    segments[1].p_flags = PF_R | PF_W; // Permissions : lecture et écriture
    segments[1].p_align = 0x1000; // Alignement

    // Mettre à jour l'en-tête ELF
    header->e_phnum = 2; // Deux segments
    header->e_phoff = sizeof(Elf32_Ehdr); // Offset des en-têtes de programme
    header->e_entry = base_address + get_adress_text(*header, section_table); // Point d'entrée
    header->e_phentsize = sizeof(Elf32_Phdr); // Taille d'un en-tête de programme

    printf("Segments de programme créés avec succès\n");
    return segments;
}

//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//


void convert_endian_segment(Elf32_Phdr *segment) {
    swap_bytes(&segment->p_type, sizeof(segment->p_type));
    swap_bytes(&segment->p_offset, sizeof(segment->p_offset));
    swap_bytes(&segment->p_vaddr, sizeof(segment->p_vaddr));
    swap_bytes(&segment->p_paddr, sizeof(segment->p_paddr));
    swap_bytes(&segment->p_filesz, sizeof(segment->p_filesz));
    swap_bytes(&segment->p_memsz, sizeof(segment->p_memsz));
    swap_bytes(&segment->p_flags, sizeof(segment->p_flags));
    swap_bytes(&segment->p_align, sizeof(segment->p_align));
}

//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//

void write_segment(FILE *fichier, Elf32_Phdr *segments, int num_segments, Elf32_Off phoff) {
    // Vérifiez que le fichier est valide
    if (fichier == NULL) {
        perror("Erreur : fichier invalide");
        exit(EXIT_FAILURE);
    }

    // Vérifiez que les segments sont valides
    if (segments == NULL || num_segments <= 0) {
        fprintf(stderr, "Erreur : segments invalides\n");
        exit(EXIT_FAILURE);
    }

    // Se positionner à l'offset des en-têtes de programme
    if (fseek(fichier, phoff, SEEK_SET) != 0) {
        perror("Erreur lors du positionnement dans le fichier");
        exit(EXIT_FAILURE);
    }

    // Écrire chaque segment
    for (int i = 0; i < num_segments; i++) {
        // Convertir l'endianness si nécessaire
        convert_endian_segment(&segments[i]);

        // Écrire le segment dans le fichier
        if (fwrite(&segments[i], 1, sizeof(Elf32_Phdr), fichier) != sizeof(Elf32_Phdr)) {
            perror("Erreur lors de l'écriture du segment");
            exit(EXIT_FAILURE);
        }
    }
    printf("Segments de programme écrits avec succès\n");
}

//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//


Symtab_nom_section* renumertion_des_section( Elf32_Ehdr* en_tete, Elf32_Shdr* elf_section,  Elf32_Shdr** non_rel_sections , 
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


    return symtab_nom_section;
}


//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//


void modification(FILE* input, FILE* output, Elf32_Addr base_address) {
    // Lecture de l'en-tête et des sections 
    Elf32_Ehdr en_tete = read_header(input);
    Elf32_Shdr* sections = read_sections_table(input, en_tete);

    // Lecture de la string table, pour les noms des sections 
    char *shstrtab = read_string_table(input, en_tete, sections);

    if (!shstrtab) {
        free(sections);
        fclose(input);
        fclose(output);
        exit(1);
    }
     
    SectionHeaderTable section_table = {sections, (char *)shstrtab, en_tete.e_shnum};

    SectionHeaderTable rel_sections_table= get_relocation_sections(&section_table);

    //lecture des sections de relocation 
    SymbolTable symtab = {0};
    int symtab_index = -1;

    Elf32_Off adr = 0;
        
    for (size_t i = 0; i < section_table.size; i++) {
        if (strcmp(&shstrtab[sections[i].sh_name], ".symtab") == 0) {
            read_symbol_table(input, &en_tete, &section_table, &symtab, ".symtab");
            adr = sections[i].sh_offset;
            symtab_index = i;
        }
        if (strcmp(&shstrtab[sections[i].sh_name], ".text") == 0) {
            sections[i].sh_addr = base_address; // Utilisation de l'adresse de base pour .text
        }
        if (strcmp(&shstrtab[sections[i].sh_name], ".data") == 0) {
            sections[i].sh_addr = base_address + 0x1000; // .data à base_address + 0x1000
        }
        if (strcmp(&shstrtab[sections[i].sh_name], ".bss") == 0) {
            sections[i].sh_addr = base_address + 0x2000 + sections[i].sh_size; // .bss à base_address + 0x2000
        }
    }

    //lecture du contenu des sections sauf celles de type REL 
    unsigned char ** sections_content = read_non_rel_sections_content(input , sections, shstrtab , en_tete);
    

   
    
    // Q6 ,Q7 : La suppression des sections REL
    Elf32_Shdr* non_rel_sections[en_tete.e_shnum]; //Liste des sections après modification 
    Symtab_nom_section *symtab_nom_section = renumertion_des_section( &en_tete , sections, non_rel_sections , &symtab , shstrtab );


    // Q8 , Q9: si il y'a des relocations
    if ( section_table.size - rel_sections_table.size != section_table.size){

        //appliquer les relocations 
        apply_relocations(input, &rel_sections_table, non_rel_sections, en_tete ,symtab_index != -1 ? symtab_nom_section->symtab : NULL,
        sections_content , symtab_nom_section->table ); 
    }

    // Calculer les offsets des sections
    Elf32_Off strat_offset = en_tete.e_ehsize;

    for (int i = 0; i < en_tete.e_shnum; i++) {
        if (i == 0) {
            // Section 0 (NULL section) offset reste 0
            non_rel_sections[i]->sh_offset = 0;
            continue;
        }

        Elf32_Shdr *section = non_rel_sections[i];
        
        if (section->sh_addralign > 0 && strat_offset % section->sh_addralign != 0) {
            strat_offset += section->sh_addralign - (strat_offset % section->sh_addralign);
        }

        section->sh_offset = strat_offset;
        strat_offset += section->sh_size;

        //changer l'address de debut de programme PC
        if (strcmp(&shstrtab[sections[i].sh_name], ".text") == 0) {
            en_tete.e_entry= section->sh_offset;
        }
        
    }
    symtab_nom_section->table=symtab_nom_section->table;
    section_alocabl(en_tete, sections);
    
    Elf32_Phdr *segment = cree_segement(input, &en_tete, base_address,shstrtab); // Passage de l'adresse de base
    en_tete.e_type = ET_EXEC;   
    
    // Écriture de l'en-tête, de la nouvelle liste des sections et de la string table dans le nouveau fichier
    write_header(output, &en_tete );
    write_sections_table(output, en_tete, non_rel_sections);
    write_string_table(output, en_tete, non_rel_sections, shstrtab);
    write_sections_content( output, en_tete,  non_rel_sections , sections_content );
    write_symbol_table(output,  &symtab, adr);
    write_segment(output, segment, en_tete.e_phnum, en_tete.e_phoff);   


    free_table_symbol(&symtab);
    free(section_table.headers);
    free(section_table.shstrtab);
    free_non_rel_sections_content(en_tete, sections_content);
    
    printf("Fichier ELF modifié créé avec succès\n");
}

//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//


int main(int argc, char* argv[]) {
    if (argc < 4) {
        fprintf(stderr, "Erreur : format incorrect.\n");
        fprintf(stderr, "Utilisation : ./Executable <nom_fichier_entree> <nom_fichier_sortie> <adresse_base>\n");
        exit(EXIT_FAILURE);
    }

    // Convertir l'adresse de base en entier
    Elf32_Addr base_address = strtoul(argv[3], NULL, 16);

    FILE* input = fopen(argv[1], "rb");
    if (!input) {
        perror("Erreur d'ouverture du fichier en lecture");
        exit(EXIT_FAILURE);
    }

    FILE* output = fopen(argv[2], "wb");
    if (!output) {
        perror("Erreur d'ouverture du fichier en écriture");
        fclose(input);
        exit(EXIT_FAILURE);
    }

    modification(input, output, base_address);

    fclose(input);
    fclose(output);
   
    return 0;
}