#include "modification_elf.h"


void write_header(FILE* file, Elf32_Ehdr* en_tete) {
    if (file == NULL) {
        fprintf(stderr, "Erreur : pointeur de fichier invalide.\n");
        exit(EXIT_FAILURE);
    }

    // Convertir en big endian, si en_tete est en little endian (boutisme du système)
    if(is_system_in_little_endian()){
        convert_endian_header(en_tete);
    }

    // Écrire la nouvelle en_tête
    if (fwrite(en_tete, 1, sizeof(Elf32_Ehdr), file) != sizeof(Elf32_Ehdr)) {
        perror("Erreur lors de l'écriture de l'en-tête ELF32");
        exit(EXIT_FAILURE);
    }
    
    // Remettre en little endian (si le boutisme du système est en little endian)
    if(is_system_in_little_endian()){
        convert_endian_header(en_tete);
    }
    printf("En-tête ELF32 écrit avec succès\n");
}

//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//

void write_sections_table(FILE* output, Elf32_Ehdr en_tete, Elf32_Shdr** non_rel_sections) {

    // Convertir au big endian, si les sections sont en little endian (boutisme du système)
    if(is_system_in_little_endian()){
        for (int i = 0; i < en_tete.e_shnum; i++) {
            Elf32_Shdr* section = non_rel_sections[i];
            convert_endian_section_header(section);
        }
    }

    if (fseek(output, en_tete.e_shoff, SEEK_SET) != 0) {
        perror("Erreur lors du positionnement pour écrire les sections");
        exit(EXIT_FAILURE);
    }

    // Écrire toutes les sections dans le fichier 
    for (int i = 0; i < en_tete.e_shnum; i++) {
        if (fwrite(non_rel_sections[i], sizeof(Elf32_Shdr), 1, output) != 1) {
            perror("Erreur lors de l'écriture des en-têtes de section");
            exit(EXIT_FAILURE);
        }
    }

    // Remettre au boutisme du système 
    if(is_system_in_little_endian()){
        for (int i = 0; i < en_tete.e_shnum; i++) {
            Elf32_Shdr* section = non_rel_sections[i];
            convert_endian_section_header(section);
        }
    }
    printf("Table des sections écrite avec succès\n");
}


//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//

void write_string_table(FILE *output, Elf32_Ehdr en_tete, Elf32_Shdr** non_rel_sections, char* section_names) {
   
    // Réécrire la table des chaînes sans modification au nouveau fichier 
    Elf32_Shdr* shstrtab = non_rel_sections[en_tete.e_shstrndx];

    if (fseek(output, shstrtab->sh_offset, SEEK_SET) != 0) {
        perror("Erreur lors du positionnement pour écrire la table des noms de section");
        exit(EXIT_FAILURE);
    }
    
    if (fwrite(section_names, 1, shstrtab->sh_size, output) != shstrtab->sh_size) {
        perror("Erreur lors de l'écriture de la table des noms de section");
        exit(EXIT_FAILURE);
    }
    printf("Table des noms de section écrite avec succès\n");
}

//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//

unsigned char** read_non_rel_sections_content(FILE* input, Elf32_Shdr* sections, char* shstrtab, 
Elf32_Ehdr en_tete) {
    
    unsigned char** sections_content = calloc(en_tete.e_shnum, sizeof(unsigned char*));
    if (!sections_content) {
        perror("Erreur d'allocation (sections_content)");
        return NULL;
    }

    int dummy_section_index = -1;
    int number_of_sections = 0;

    for (int i = 0; i < en_tete.e_shnum; i++) {
        Elf32_Shdr* section = &sections[i];

        // Ignore la section si  de type relocation
        if (section->sh_type == SHT_REL) {
            continue;
        }

        // Récupère le nom de la section
        const char* section_name = &shstrtab[section->sh_name];

        // Lire le contenu 
        sections_content[number_of_sections] = read_section_content(input, section_name, en_tete, sections,
         shstrtab, &dummy_section_index
        );

        // On a lu une section non-REL
        number_of_sections++;
    
    }

    return sections_content;
}

//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//

void free_non_rel_sections_content(Elf32_Ehdr en_tete, unsigned char **sections_content) {
    if (sections_content == NULL) {
        return;
    }

    // Libère la mémoire de chaque section.
    for (int i = 0; i < en_tete.e_shnum; i++) {
        if (sections_content[i] != NULL) {
            free(sections_content[i]);
            sections_content[i] = NULL;
        }
    }

    free(sections_content);
    sections_content = NULL;
}

//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//

void write_symbol_table(FILE* output, SymbolTable* table_symbole, Elf32_Off offset) {
    if (!output) {
        fprintf(stderr, "Erreur : fichier de sortie invalide.\n");
        return;
    }

    if (!table_symbole || !table_symbole->symbols) {
        fprintf(stderr, "Erreur : Table des symboles invalide.\n");
        return;
    }

    if (table_symbole->size == 0) {
        fprintf(stderr, "Erreur : La table des symboles est vide.\n");
        return;
    }


    // Déplacement du curseur au bon offset
    if (fseek(output, offset, SEEK_SET) != 0) {
        perror("Erreur lors du déplacement dans le fichier");
        return;
    }

    // Écriture des symboles
    for (size_t i = 0; i < table_symbole->size; i++) {
        Elf32_Sym* symbol = &table_symbole->symbols[i];

        if (is_system_in_little_endian()) {
            convert_endian_symbol(symbol);
        }

        if (fwrite(symbol, sizeof(Elf32_Sym), 1, output) != 1) {
            perror("Erreur lors de l'écriture d'un symbole");
            return;
        }

    }
    printf("Table des symboles écrite avec succès\n");
}

//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//

void write_sections_content(FILE* output,  Elf32_Ehdr en_tete, Elf32_Shdr** non_rel_sections , unsigned char** sections_content ) {
    
    for (int i = 0; i < en_tete.e_shnum; i++) {

        Elf32_Shdr* section = non_rel_sections[i];
        unsigned char* section_content=sections_content[i];


        // Positionner le fichier de sortie à l'offset de la section
        if (fseek(output, section->sh_offset, SEEK_SET) != 0) {
            perror("Erreur lors du positionnement pour écrire le contenu de la section");
            free_non_rel_sections_content(en_tete, sections_content);
            exit(EXIT_FAILURE);
        }

        // Écrire le contenu de la section dans le fichier de sortie
        if (fwrite(section_content, 1, section->sh_size, output) != section->sh_size) {
            perror("Erreur lors de l'écriture du contenu de la section");
            free_non_rel_sections_content(en_tete, sections_content);
            exit(EXIT_FAILURE);
        }

    }
    printf("Contenu des sections écrit avec succès\n");
}




//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//



char* delete_rel_sections_names(char** string_table,
                               Elf32_Ehdr en_tete,
                               int size,
                               Elf32_Shdr* sections,
                               Elf32_Shdr** non_rel_sections){
   
    Elf32_Shdr shstrtab = sections[en_tete.e_shstrndx];

    // Allouer la mémoire , calloc pour initialiser à zéro
    char* new_string_table = calloc(shstrtab.sh_size, 1);
    if (!new_string_table) {
        fprintf(stderr, "Erreur d'allocation de mémoire pour la nouvelle table de strings\n");
        exit(1);
    }

    new_string_table[0] = '\0';

    // La taille initiale prend en compte le premier caractère nul
    int new_string_table_size = 1;

    // j : index pour non_rel_sections
    int j = 0;

    // Parcourir toutes les sections
    for (int i = 0; i < size; i++) {
        
        // Si la section n'est pas de type REL (SHT_REL ou SHT_RELA)
        if (sections[i].sh_type != SHT_REL && sections[i].sh_type != SHT_RELA) {

            // Récupérer l'offset du nom dans la vieille string table
            int name_offset = sections[i].sh_name;

            // Récupérer le nom depuis l'ancienne table
            const char* section_name = &(*string_table)[name_offset];

            // Copier le nom dans la section
            strcpy(&new_string_table[new_string_table_size], section_name);

            // Mettre à jour l'offset sh_name dans non_rel_sections
            non_rel_sections[j]->sh_name = new_string_table_size;

            j++;

            // Avancer l'offset de nom de la section dans la nouvelle table
            new_string_table_size += strlen(section_name) + 1;
        }
    }

    //Mettre à jour la taille de la section string table
    non_rel_sections[en_tete.e_shstrndx]->sh_size = new_string_table_size;

    // Retourner la nouvelle string table
    return new_string_table;
}



//**********************************************************************************************************************************************//
//**********************************************************************************************************************************************//

void modification_elf(FILE* input, FILE* output){

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

    //lecture des sections de relocation 
    SectionHeaderTable rel_sections_table= get_relocation_sections(&section_table);

    SymbolTable symtab = {0};
    int symtab_index = -1;
    Elf32_Off adr = 0;
        
    for (size_t i = 0; i < section_table.size; i++) {
        if (strcmp(&shstrtab[sections[i].sh_name], ".symtab") == 0) {
            read_symbol_table(input, &en_tete, &section_table, &symtab, ".symtab");
            adr = sections[i].sh_offset;
            symtab_index = i;
        }
    }

    //lecture du contenu des sections sauf celles de type REL 
    unsigned char ** sections_content = read_non_rel_sections_content(input , sections, shstrtab , 
    en_tete);

     // Q6 ,Q7 : La suppression des sections REL
    Elf32_Shdr* non_rel_sections[en_tete.e_shnum]; //Liste des sections après modification 
    Symtab_nom_section *symtab_nom_section = renumertion_des_sections( &en_tete , sections, non_rel_sections , &symtab , shstrtab ); //e_shenum modifié


    //nom_indice_section_table *table = malloc (sizeof(nom_indice_section_table));    

    // Q8 et Q9: si il y'a des relocations
    if ( section_table.size - rel_sections_table.size != section_table.size){

        //appliquer les relocations 
        apply_relocations(input, &rel_sections_table, non_rel_sections, en_tete ,symtab_index != -1 ? symtab_nom_section->symtab : NULL,
        sections_content , symtab_nom_section->table ); 
    }




    // Définir de nouveaux offsets pour éviter les trous 
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


   

    // Écriture de l'en-tête, de la nouvelle liste des sections et de la string table dans le nouveau fichier
    write_header(output, &en_tete );
    write_sections_table(output, en_tete, non_rel_sections);
    write_string_table(output, en_tete, non_rel_sections, shstrtab);
    write_sections_content( output, en_tete,  non_rel_sections , sections_content );
    write_symbol_table(output, symtab_nom_section->symtab, adr);


    free_table_symbol(&symtab);
    free(section_table.headers);
    free(rel_sections_table.headers);
    free(section_table.shstrtab);
    free_non_rel_sections_content(en_tete, sections_content);
    printf("Fichier ELF modifié avec succès\n");
}

