
#include "ARM_elf.h"

//Chargement du code ELF dans un simulateur ARM et exécution du programme
void code_run(char *hostname, char *servicename , char* file_name) {
    //ouvrir le fichier l'entree
   FILE* input = fopen(file_name, "rb");

    if (input == NULL) {
        fprintf(stderr, "Error opening file: %s\n", file_name);
        exit(1);
    }

    // Lecture de l'en-tête et des sections headers
    Elf32_Ehdr en_tete = read_header(input);
    Elf32_Shdr* sections = read_sections_table(input, en_tete);

    // Lecture de la string table, pour les noms des sections 
    char *shstrtab = read_string_table(input, en_tete, sections);

    if (!shstrtab) {
        free(sections);
        fclose(input);
        exit(1);
    }

    // adresse ou ecrire dans la memoire 
    uint32_t address_debut= en_tete.e_entry;
	uint32_t address = address_debut ;
    arm_simulator_data_t simulator;

    //connexion au simulator
    debug("Connecting to simulator using host %s and service %s\n", hostname, servicename);
	simulator = arm_connect(hostname, servicename);
	debug("Fetching code to simulator and setting PC\n");


    //pour l'ecriture de contenu des sections allouable dans la memoire
    unsigned char* section_content;
    int dummy_section_index;

    for (int i = 0; i < en_tete.e_shnum; i++) {
        Elf32_Shdr* section = &sections[i];

        // si section est allouable
        if (section->sh_flags & 0x2){

            // Récupère le nom de la section
            const char* section_name = &shstrtab[section->sh_name];

            // Lire son contenu 
            dummy_section_index = -1;
            section_content= read_section_content(input, section_name, en_tete, sections, shstrtab, &dummy_section_index);

            //la taille du contenu
            size_t size=section->sh_size;

            //ecrire le contenu de la section dans la memoire
            arm_write_memory(simulator, address, section_content, size);

            //liberer l'espace allouer pour section_content 
            free(section_content);
           
            // calculer l'addresse pour les sections apres 
            address += size;
            
        }
    }

    //mettre pc au addresse de debut
    arm_write_register(simulator, Program_Counter , address_debut);
    debug("Running simulator\n");
	arm_run(simulator);
	debug("End of simulation\n");

    //liberer l'espace allouer et fermer le fichier 
    free(sections);
    free(shstrtab);
    fclose(input); 
}

//************************************************************************************************************************//
//************************************************************************************************************************//

//Affichage du mode d'emploi de l'application
void usage(char *name) {
	fprintf(stderr, "Usage:\n"
		"%s [ --help ] [ --host hostname ] [ --service servicename ] [ --debug file ] file\n\n"
		"Loads a sample ARM code to a remote simulator. The --debug flag enables the output produced by "
		"calls to the debug function in the named source file.\n" 
		, name);
}


//************************************************************************************************************************//
//************************************************************************************************************************//



int main(int argc, char *argv[]) {
	int opt;
	char *hostname, *servicename;

	struct option longopts[] = {
		{ "debug", required_argument, NULL, 'd' },
		{ "host", required_argument, NULL, 'H' },
		{ "service", required_argument, NULL, 'S' },
		{ "help", no_argument, NULL, 'h' },
		{ NULL, 0, NULL, 0 }
	};

	hostname = NULL;
	servicename = NULL;
	while ((opt = getopt_long(argc, argv, "S:H:d:h", longopts, NULL)) != -1) {
		switch(opt) {
		case 'H':
			hostname = optarg;
			break;
		case 'S':
			servicename = optarg;
			break;
		case 'h':
			usage(argv[0]);
			exit(0);
		case 'd':
			add_debug_to(optarg);
			break;
		default:
			fprintf(stderr, "Unrecognized option %c\n", opt);
			usage(argv[0]);
			exit(1);
		}
	}
	code_run(hostname, servicename , argv[argc-1]) ;
	return 0;
}
