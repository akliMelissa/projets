#ifndef _RENUMERATION_DES_SECTIONS_H
#define _RENUMERATION_DES_SECTIONS_H

#include "affichage_table_reimplantation.h" 
#include "Correction_des_symboles.h"


//Pour la suppression des sections REL et renumeration des sections
Symtab_nom_section *renumertion_des_sections( Elf32_Ehdr* en_tete, Elf32_Shdr* elf_section,  Elf32_Shdr** non_rel_sections , 
    SymbolTable *symtab ,char *section_names  ) ;


#endif 

