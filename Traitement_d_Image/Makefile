#############################################################################
# Fichier Makefile 
# UE MAP401 - DLST - UGA - 2020/2021
#############################################################################
# utilisation des variables internes $@ $< $^ $*
# $@ : correspond au nom de la cible
# $< : correspond au nom de la premiere dependance
# $^ : correspond � toutes les d�pendances
# $* : correspond au nom du fichier sans extension 
#       (dans les regles generiques uniquement)
#############################################################################
# information sur la regle executee avec la commande @echo
# (une commande commancant par @ n'est pas affichee a l'ecran)
#############################################################################


#############################################################################
# definition des variables locales
#############################################################################

# compilateur C
CC = clang

# chemin d'acces aux librairies (interfaces)
INCDIR = .

# chemin d'acces aux librairies (binaires)
LIBDIR = .

# options pour l'�dition des liens
LDOPTS = -L$(LIBDIR) -lm

# options pour la recherche des fichiers .o et .h
INCLUDEOPTS = -I$(INCDIR)

# options de compilation
COMPILOPTS = -g -Wall $(INCLUDEOPTS)

# liste des executables
EXECUTABLES = test_image test_geom2d test_tache3_2 test_tache4 test_tache5_1 test_tache5_2 \
 			test_tache6_1 test_tache6_2 test_tache7_1 test_tache7_2 tache8_1 tache8_2 


#############################################################################
# definition des regles
#############################################################################

########################################################
# la r�gle par d�faut
all : $(EXECUTABLES)

########################################################
# regle generique : 
#  remplace les regles de compilation separee de la forme
#	module.o : module.c module.h
#		$(CC) -c $(COMPILOPTS) module.c
%.o : %.c %.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module "$*
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<

########################################################
# regles explicites de compilation separee de modules
# n'ayant pas de fichier .h ET/OU dependant d'autres modules
image.o : image.c image.h types_macros.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module image"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<


geom2d.o : geom2d.c geom2d.h 
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module geom2d"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<


sequence.o : sequence.c sequence.h geom2d.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module sequence"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<

Bezier.o : Bezier.c Bezier.h geom2d.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module Bezier"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<
		
robot.o : robot.c robot.h image.h  geom2d.h sequence.h Bezier.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module robot"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<	

files_generation.o : files_generation.c files_generation.h  geom2d.h sequence.h Bezier.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module files_generation"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<	

test_image.o : test_image.c image.h 
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module test_image"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<



test_tache3.o : test_tache3.c robot.h image.h sequence.h files_generation.h Bezier.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module test_tache3"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<

test_tache4.o : test_tache4.c robot.h image.h sequence.h files_generation.h Bezier.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module test_tache4"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<		

test_tache5.o : test_tache5.c robot.h image.h sequence.h files_generation.h Bezier.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module test_tache5"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<

test_tache5B.o : test_tache5B.c robot.h image.h sequence.h files_generation.h Bezier.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module test_tache5"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<

test_tache6_partie1.o : test_tache6_partie1.c robot.h image.h sequence.h Bezier.h files_generation.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module test_tache6_partie1"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<

test_tache6.o : test_tache6.c robot.h image.h sequence.h Bezier.h files_generation.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module test_tache6"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<

test_tache7_1.o : test_tache7_1.c robot.h image.h sequence.h Bezier.h files_generation.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module test_tache7"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<

test_tache7_2.o : test_tache7_2.c robot.h image.h sequence.h Bezier.h files_generation.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module test_tache7"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<	
	

tache8_1.o : tache8_1.c robot.h image.h sequence.h Bezier.h files_generation.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module tache8_1"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<	

	
tache8_2.o : tache8_2.c robot.h image.h sequence.h Bezier.h files_generation.h
	@echo ""
	@echo "---------------------------------------------"
	@echo "Compilation du module tache8_2"
	@echo "---------------------------------------------"
	$(CC) -c $(COMPILOPTS) $<	


	

########################################################
# regles explicites de creation des executables


test_image: test_image.o image.o 
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@

test_geom2d : test_geom2d.o geom2d.o 
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@


test_tache3_2 : test_tache3.o robot.o image.o  geom2d.o  sequence.o files_generation.o Bezier.o
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@



test_tache4 : test_tache4.o robot.o image.o  geom2d.o  sequence.o  files_generation.o Bezier.o
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@

test_tache5_1 : test_tache5.o robot.o image.o  geom2d.o  sequence.o files_generation.o Bezier.o
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@

test_tache5_2 : test_tache5B.o robot.o image.o  geom2d.o  sequence.o files_generation.o Bezier.o
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@
	

	
test_tache6_1 : test_tache6_partie1.o robot.o image.o  geom2d.o  sequence.o Bezier.o files_generation.o
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable tache6_partie1 "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@

test_tache6_2 : test_tache6.o robot.o image.o  geom2d.o  sequence.o Bezier.o files_generation.o
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable tache6 "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@


test_tache7_1 : test_tache7_1.o robot.o image.o  geom2d.o  sequence.o Bezier.o files_generation.o
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable tache7 "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@

test_tache7_2 : test_tache7_2.o robot.o image.o  geom2d.o  sequence.o Bezier.o files_generation.o
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable tache7 "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@

tache8_2: tache8_2.o robot.o image.o  geom2d.o  sequence.o Bezier.o files_generation.o
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable tache8 "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@

tache8_1: tache8_1.o robot.o image.o  geom2d.o  sequence.o Bezier.o files_generation.o
	@echo ""
	@echo "---------------------------------------------"
	@echo "Creation de l'executable tache8 "$@
	@echo "---------------------------------------------"
	$(CC) $^ $(LDOPTS) -o $@


# regle pour "nettoyer" le r�pertoire
clean:
	rm -fR $(EXECUTABLES) *.o 
