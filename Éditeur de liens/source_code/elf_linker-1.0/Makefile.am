ACLOCAL_AMFLAGS = -I m4

# Ajout de l'option subdir-objects
AUTOMAKE_OPTIONS = subdir-objects

AM_CFLAGS=-Wall -Werror -g
AM_CFLAGS+=-D DEBUG
AM_CFLAGS+=-D WARNING
AM_CFLAGS+=-D BIG_ENDIAN_SIMULATOR

LDADD=-lpthread

SUBDIRS=
SUBDIRS+=. Examples_loader

bin_PROGRAMS=
bin_PROGRAMS+=ARM_runner_example
bin_PROGRAMS+=Program
bin_PROGRAMS+=Program2
bin_PROGRAMS+=Executable
bin_PROGRAMS+=test_elf_coherence
bin_PROGRAMS+=ARM_elf


AM_CPPFLAGS = -I$(top_srcdir)/include -I$(top_srcdir)/include/affichage

ENTETE=include 

COMMON=debug.h debug.c util.h util.c
COMMONPHASE1= $(ENTETE)/affichage_contenu_section.h $(ENTETE)/affichage_entete.h $(ENTETE)/affichage_table_reimplantation.h $(ENTETE)/affichage_table_section.h $(ENTETE)/affichage_table_symbole.h 
COMMONPHASE2= $(ENTETE)/affichage_contenu_section.h $(ENTETE)/affichage_entete.h $(ENTETE)/affichage_table_reimplantation.h $(ENTETE)/affichage_table_section.h $(ENTETE)/affichage_table_symbole.h  $(ENTETE)/renumeration_des_sections.h $(ENTETE)/appliquation_des_reimplantations.h $(ENTETE)/modification_elf.h $(ENTETE)/Correction_des_symboles.h  $(ENTETE)/Call_Jump.h $(ENTETE)/debug.h $(ENTETE)/test_sections.h
COMMONSIMILATOR= ARM_elf.h

# Sources communes pour les tests
TEST_COMMON_SOURCES = src/modification_elf.c \
                     src/affichage_entete.c \
                     src/affichage_table_section.c \
                     src/affichage_contenu_section.c \
                     src/affichage_table_symbole.c \
                     src/affichage_table_reimplantation.c \
                     src/renumeration_des_sections.c \
                     src/appliquation_des_reimplantations.c \
                     src/Correction_des_symboles.c \
                     src/Call_Jump.c \
                     test/test_renumeration_des_sections/test_sections.c

# Sources spécifiques pour test_elf_coherence
TEST_COHERENCE_SOURCES = test/test_renumeration_des_sections/test_elf_coherence.c

# Sources spécifiques pour test_correction_des_symboles
TEST_CORRECTION_SOURCES = test/test_correction_des_symboles/test_correction_des_symboles.c

EXTRA_DIST=filtre.pl
SIMULATOR_INTERFACE=csapp.h csapp.c scanner.h scanner.l gdb_protocol.h gdb_protocol.c arm_simulator_interface.h arm_simulator_interface.c

ARM_runner_example_SOURCES=$(COMMON) $(SIMULATOR_INTERFACE) ARM_runner_example.c
ARM_elf_SOURCES=$(COMMON) $(COMMONSIMILATOR) src/affichage_entete.c src/affichage_table_section.c src/affichage_contenu_section.c  $(SIMULATOR_INTERFACE) ARM_elf.c

Program_SOURCES=$(COMMONPHASE1) src/Program.c src/affichage_entete.c src/affichage_table_section.c src/affichage_contenu_section.c src/affichage_table_symbole.c src/affichage_table_reimplantation.c 
Program2_SOURCES=$(COMMONPHASE2) src/Program2.c src/affichage_entete.c src/affichage_table_section.c src/affichage_contenu_section.c src/affichage_table_symbole.c src/affichage_table_reimplantation.c src/renumeration_des_sections.c src/appliquation_des_reimplantations.c src/modification_elf.c src/Correction_des_symboles.c src/Call_Jump.c
Executable_SOURCES=$(COMMONPHASE2) src/Executable.c src/affichage_entete.c src/affichage_table_section.c src/affichage_contenu_section.c src/affichage_table_symbole.c src/affichage_table_reimplantation.c src/renumeration_des_sections.c src/appliquation_des_reimplantations.c src/modification_elf.c src/Correction_des_symboles.c src/Call_Jump.c

test_elf_coherence_SOURCES=$(COMMONPHASE2) $(TEST_COMMON_SOURCES) $(TEST_COHERENCE_SOURCES)
test_correction_des_symboles_SOURCES=$(COMMONPHASE2) $(TEST_COMMON_SOURCES) $(TEST_CORRECTION_SOURCES)

EXTRA_DIST+=sample_run.sh
EXTRA_DIST+=programme_run.sh



