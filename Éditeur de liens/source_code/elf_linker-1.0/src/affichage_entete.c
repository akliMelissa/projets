#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <elf.h>


const char* get_elf_class(unsigned char class) {
    switch (class) {
        case 1: return "ELF32";
        case 2: return "ELF64";
        default: return "Inconnu";
    }
}

//********************************************************************************************************//
//********************************************************************************************************//


const char* get_elf_type(Elf32_Half e_type) {
    switch (e_type) {
        case 0: return "No file type (ET_NONE)";
        case 1: return "REL (Relocatable file)";
        case 2: return "Executable file (ET_EXEC)";
        case 3: return "Shared object file (ET_DYN)";
        case 4: return "Core file (ET_CORE)";
        case 0xff00: return "Processor-specific type (ET_LOPROC)";
        case 0xffff: return "Processor-specific type (ET_HIPROC)";
        default: return "Unknown type";
    }
}

//********************************************************************************************************//
//********************************************************************************************************//


const char* get_elf_endianness(unsigned char data) {
    switch (data) {
        case 1: return "2's complement, little Endian";
        case 2: return "2's complement, big Endian";
        default: return "Inconnu";
    }
}

//********************************************************************************************************//
//********************************************************************************************************//


const char* get_elf_version(unsigned char data) {
    switch (data) {
        case 1: return "1 (current)";
        case 2: return "0 (Invalid)";
        default: return "Inconnu";
    }
}

//********************************************************************************************************//
//********************************************************************************************************//


const char* get_elf_machine(uint16_t machine) {
    switch (machine) {
        case 0x00: return "No specific machine (ET_NONE)";
        case 0x02: return "SPARC";
        case 0x03: return "Intel 80386";
        case 0x08: return "MIPS";
        case 0x14: return "PowerPC";
        case 0x16: return "PowerPC64";
        case 0x28: return "ARM";
        case 0x2A: return "SuperH";
        case 0x32: return "IA-64";
        case 0x3E: return "Advanced Micro Devices X86-64";
        case 0xB7: return "AArch64 (ARM64)";
        case 0xF3: return "RISC-V";
        case 0x183: return "Texas Instruments TMS320C6000";
        case 0x04: return "Motorola 68000 (M68k)";
        case 0x06: return "Intel 80860";
        case 0x07: return "MIPS RS3000";
        case 0x24: return "Alpha (DEC)";
        case 0x66: return "STMicroelectronics ST9+";
        case 0x67: return "STMicroelectronics ST7";
        case 0x68: return "Motorola MC68HC16";
        case 0x69: return "Motorola MC68HC11";
        case 0x6A: return "Motorola MC68HC08";
        case 0x6B: return "Motorola MC68HC05";
        case 0x8C: return "Zilog Z80";
        case 0x9026: return "MicroBlaze";
        case 0x9041: return "Renesas RX";
        case 0xB9: return "OpenRISC";
        case 0xDC: return "Microchip PIC32";
        default: return "Unknown machine type";
    }
}

//********************************************************************************************************//
//********************************************************************************************************//


//Pour le boutisme de systeme utilisé : return  1 si little endian , 0 sinon
int is_system_in_little_endian(){
    unsigned int x=1;
    return *((unsigned char* )&x)==1;
}

//********************************************************************************************************//
//********************************************************************************************************//


// converter les octets 
void swap_bytes(void *data, size_t size) {
    unsigned char *byte_data = (unsigned char *)data;

    for (size_t i = 0; i < size / 2; i++) {
        unsigned char temp = byte_data[i];
        byte_data[i] = byte_data[size - 1 - i];
        byte_data[size - 1 - i] = temp;
    }
}

//********************************************************************************************************//
//********************************************************************************************************//


void convert_endian_header(Elf32_Ehdr* en_tete) {         
    swap_bytes(&en_tete->e_type, sizeof(en_tete->e_type));   
    swap_bytes(&en_tete->e_machine, sizeof(en_tete->e_machine)); 
    swap_bytes(&en_tete->e_version, sizeof(en_tete->e_version));    
    swap_bytes(&en_tete->e_entry, sizeof(en_tete->e_entry));        
    swap_bytes(&en_tete->e_phoff, sizeof(en_tete->e_phoff));        
    swap_bytes(&en_tete->e_shoff, sizeof(en_tete->e_shoff));        
    swap_bytes(&en_tete->e_flags, sizeof(en_tete->e_flags));        
    swap_bytes(&en_tete->e_ehsize, sizeof(en_tete->e_ehsize));      
    swap_bytes(&en_tete->e_phentsize, sizeof(en_tete->e_phentsize));
    swap_bytes(&en_tete->e_phnum, sizeof(en_tete->e_phnum));        
    swap_bytes(&en_tete->e_shentsize, sizeof(en_tete->e_shentsize));
    swap_bytes(&en_tete->e_shnum, sizeof(en_tete->e_shnum));       
    swap_bytes(&en_tete->e_shstrndx, sizeof(en_tete->e_shstrndx));  
}

//********************************************************************************************************//
//********************************************************************************************************//


Elf32_Ehdr read_header(FILE* file){

    // lecture de l'en tete
    Elf32_Ehdr en_tete;

    if (fseek(file, 0, SEEK_SET) != 0) {
        perror("Erreur de positionnement pour la lecture de l'en-tête ELF32");
        exit(EXIT_FAILURE);
    }

    if (fread(&en_tete, 1, sizeof(Elf32_Ehdr), file) != sizeof(Elf32_Ehdr)) {
        perror("Erreur de lecture de l'en-tête ELF32");
        exit(EXIT_FAILURE);
    }

    //convertaire le boutisme
    if(is_system_in_little_endian() && en_tete.e_ident[EI_DATA]==2){
        convert_endian_header(&en_tete);
    }
    else if( ! is_system_in_little_endian() &&  en_tete.e_ident[EI_DATA]==1){
        convert_endian_header(&en_tete);
    }

    return en_tete;
}

//********************************************************************************************************//
//********************************************************************************************************//


void display_header(Elf32_Ehdr en_tete) {
   
    printf("\n--- ELF Header ---\n");
    printf("  Magic:   ");

    for (int i = 0; i < 16; i++) {
        printf("%02x ", en_tete.e_ident[i]);
    }
    printf("\n");

    printf("  Class:                             %s\n", get_elf_class(en_tete.e_ident[EI_CLASS]));
    printf("  Data:                              %s\n", get_elf_endianness(en_tete.e_ident[EI_DATA]));
    printf("  Version:                           %s\n", get_elf_version(en_tete.e_version));
    printf("  Type:                              %s\n", get_elf_type(en_tete.e_type));
    printf("  Machine:                           %s\n", get_elf_machine(en_tete.e_machine));
    printf("  Entry point address:               0x%x\n", en_tete.e_entry);
    printf("  Start of program headers:          %u (bytes into file)\n", en_tete.e_phoff);
    printf("  Start of section headers:          %u (bytes into file)\n", en_tete.e_shoff);
    printf("  Flags:                             0x%x, Version5 EABI\n", en_tete.e_flags);
    printf("  Size of this header:               %d (bytes)\n", en_tete.e_ehsize);
    printf("  Size of program headers:           %d (bytes)\n", en_tete.e_phentsize);
    printf("  Number of program header:          %d\n", en_tete.e_phnum);
    printf("  Size of section header:            %d (bytes)\n", en_tete.e_shentsize);
    printf("  Number of section header:          %d\n", en_tete.e_shnum);
    printf("  Section header string table index: %d\n", en_tete.e_shstrndx);
}
