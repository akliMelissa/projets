#include "appliquation_des_reimplantations.h"
#include "Call_Jump.h"


Elf32_Addr offset_relatif(FILE* file, SymbolTable *symtab, int symbol_index , Elf32_Addr offset ){
    // Déclare une variable pour le nouvel offset, initialisé à l'offset d'entrée
    Elf32_Addr New_offset = offset ;

    // Calcule l'adresse du symbole en utilisant lookup_symbol, puis soustrait l'offset
    New_offset = lookup_symbol(symtab, symbol_index)-New_offset;

    // Si l'instruction est de type Thumb, applique un masque spécifique à l'offset
    if (is_thumb_instruction(&symtab->symbols[symbol_index])) {
       New_offset &= 0x01FFFFFE;
    }
    else{
        // Si l'instruction est de type ARM, applique un masque différent
        New_offset &= 0x03FFFFFE;
    }

    // Décale l'offset de 2 bits à droite pour ajuster la valeur
    New_offset = New_offset >> 2    ;

    fprintf(file, "Valeur de symbol : 0x%08X\n", lookup_symbol(symtab, symbol_index));
    
    return New_offset;
}

//************************************************************************************************************//
//************************************************************************************************************//


void write_Call(size_t offset ,unsigned char** sections_content, int section_index, Elf32_Addr Call_offset){
    // Récupère les données de la section spécifiée par section_index
    unsigned char* data = sections_content[section_index];

    // Ajoute un masque pour indiquer que l'instruction est un appel (bl) ARM avec l'offset fourni
    Call_offset = 0xEB000000 | Call_offset;

    // Écrit les 4 octets de l'adresse de l'appel dans la section, en les découpant sur 4 octets
    data[offset]     = (Call_offset >> 24) & 0xFF; 
    data[offset + 1] = (Call_offset >> 16) & 0xFF;
    data[offset + 2] = (Call_offset >> 8) & 0xFF;
    data[offset + 3] = Call_offset & 0xFF;  
    
}

//************************************************************************************************************//
//************************************************************************************************************//


void write_Jump(size_t offset ,unsigned char** sections_content, int section_index, Elf32_Addr jump_offset ){
    // Récupère les données de la section spécifiée par section_index
    unsigned char* data = sections_content[section_index];

    // Ajoute un masque pour indiquer que l'instruction est un saut (b) ARM avec l'offset fourni
    jump_offset = 0xEA000000 | jump_offset; 

    // Écrit les 4 octets de l'adresse du saut dans la section, en les découpant sur 4 octets
    data[offset]     = (jump_offset >> 24) & 0xFF; 
    data[offset + 1] = (jump_offset >> 16) & 0xFF;
    data[offset + 2] = (jump_offset >> 8) & 0xFF;
    data[offset + 3] = jump_offset & 0xFF;
    
}

//************************************************************************************************************//
//************************************************************************************************************//


void reimplantation_Call_Jump(FILE* file, 
    int section_index, 
    Elf32_Rel *entry,
    unsigned char **sections_content,
    SymbolTable *symtab ,
    char * section_name ){

    // Récupère l'offset de la relocalisation à partir de l'entrée de relocalisation
    Elf32_Addr offset = entry->r_offset;
    fprintf(file, "Offset_rel: 0x%08X\n", offset);

    // Récupère le type de relocalisation à partir de l'info de relocalisation
    int relocation_type = ELF32_R_TYPE(entry->r_info);

    // Récupère l'index du symbole à partir de l'info de relocalisation
    int symbol_index    = ELF32_R_SYM(entry->r_info);

    //((S + A) | T) – P
    Elf32_Addr New_offset = offset_relatif(file, symtab, symbol_index , offset);
    
    // Traitement en fonction du type de relocalisation
    switch (relocation_type){
    case R_ARM_CALL:
        fprintf(file, "R_ARM_CALL\n");
        write_Call(offset, sections_content, section_index, New_offset);
        break;
    case R_ARM_JUMP24:
        fprintf(file, "R_ARM_JUMP24\n");
        write_Jump(offset, sections_content, section_index, New_offset);
        break;
    default:
        break;
    }

    

}

