#include <assert.h>
#include <string.h>
#include <elf.h>
#include <stdio.h>
#include "affichage_entete.h"
#include "test_sections.h"

void    test_elf_modifications(FILE* output) {
    // 1. Vérification de l'en-tête et des sections
    Elf32_Ehdr header = read_header(output);
    Elf32_Shdr* sections = read_sections_table(output, header);
    char* shstrtab = read_string_table(output, header, sections);

    // 2. Vérifier qu'il n'y a plus de sections REL
    for (int i = 0; i < header.e_shnum; i++) {
        assert(sections[i].sh_type != SHT_REL && "Une section REL existe encore");
    }

    // 3. Vérifier la cohérence des offsets et tailles
    Elf32_Off current_offset = header.e_ehsize;  // Commence après l'en-tête ELF
    
    for (int i = 0; i < header.e_shnum; i++) {
        if (sections[i].sh_type != SHT_NOBITS && sections[i].sh_size > 0) {
            // Vérifier l'alignement
            if (sections[i].sh_addralign > 0) {
                assert((sections[i].sh_offset % sections[i].sh_addralign) == 0 &&
                       "Mauvais alignement de section");
            }
            
            // Vérifier que l'offset est cohérent
            assert(sections[i].sh_offset >= current_offset &&
                   "Chevauchement de sections détecté");
            
            current_offset = sections[i].sh_offset + sections[i].sh_size;
        }
    }

    // 4. Vérifier la cohérence des indices dans l'en-tête
    assert(header.e_shnum > 0 && "Nombre de sections invalide");
    assert(header.e_shstrndx < header.e_shnum && 
           "Index de la table des chaînes invalide");

    // 5. Vérifier la table des symboles si elle existe
    for (int i = 0; i < header.e_shnum; i++) {
        if (sections[i].sh_type == SHT_SYMTAB) {
            assert(sections[i].sh_entsize == sizeof(Elf32_Sym) &&
                   "Taille d'entrée de symbole incorrecte");
            assert(sections[i].sh_link < header.e_shnum &&
                   "Lien de section symbole invalide");
        }
    }

    // 6. Vérifier que les noms de sections sont valides
    for (int i = 0; i < header.e_shnum; i++) {
        if (sections[i].sh_name > 0) {
            assert(sections[i].sh_name < sections[header.e_shstrndx].sh_size &&
                   "Index de nom de section invalide");
        }
    }
    //rechercher indice de strtab
    int strtab_index = -1;
    for (int i = 0; i < header.e_shnum; i++) {
        if (strcmp(&shstrtab[sections[i].sh_name], ".strtab") == 0) {
            strtab_index = i;
            break;
        }
    }
    printf("strtab_index = %d\n", strtab_index);
    if (strtab_index == -1) {
        fprintf(stderr, "Pas de section .strtab trouvée\n");
        exit(1);
    }

    // Vérification de la liaison correcte entre .symtab et .strtab
    for (int i = 0; i < header.e_shnum; i++) {
     if (strcmp(&shstrtab[sections[i].sh_name], ".symtab") == 0) {
            assert(sections[i].sh_link == strtab_index && "Le champ sh_link de .symtab ne correspond pas à .strtab");
        }
    }


    // Libération de la mémoire
    free(sections);
    free(shstrtab);
}