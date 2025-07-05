# Utilise javac pour compiler
CC=javac

# Répertoire source et sortie
PROJET=ProjetOnitama
SRC=$(PROJET)/src
OUT=ProjetOnitama/out

OUT_INTELLIJ=ProjetOnitama/out/production/ProjetOnitama

# Cible par défaut
.PHONY: doc copy_res jar_clean clean_docu clean_out

Help:
	@echo "Commande du Makefile:"
	@echo "- make help :"
	@echo "	Permet d'afficher l'aide du makefile"
	@echo "- make Onitama_out:"
	@echo "	Permet de compiler et copier les fichier ressource pour le jeu Onitama"
	@echo "- make Onitama_compiler:"
	@echo "	Permet de compiler les fichier sources"
	@echo "- make copy_res:"
	@echo "	Permet de copier les ressources stocké dans res pour le jeu Onitama"
	@echo "- make Onitama_exec:"
	@echo " Permet de lancer le fichier Onitama"
	@echo "- make init_doc:"
	@echo "	Permet de créer le fichier de configuration de doxygene"
	@echo "- make doc:"
	@echo "	Permet de générer la documentation nécéssite de configurer le fichier doxygen"
	@echo "- make jar_create:"
	@echo "	Permet de créer un fichier jar du jeu Onitama."
	@echo "- make jar_lunch:"
	@echo "	Permet de lancé le jeu avec le fichier jar."
	@echo "- make clean_jar:"
	@echo "	Permet de suprimmer Onitama.jar"
	@echo "- make clean_docu"
	@echo "	Permet de supprimer la documentation"
	@echo "- make clean_out"
	@echo "	Permet de supprimer les fichier compiler"

Onitama_out: Onitama_compile copy-res

Onitama_compile:
	mkdir -p $(OUT_INTELLIJ)
	$(CC) --release 11 -d $(OUT_INTELLIJ) $(shell find $(SRC) -name "*.java")

copy-res:
	cp -r $(PROJET)/res/* $(OUT_INTELLIJ)
	cp -r $(PROJET)/res/.Cartes.txt $(OUT_INTELLIJ)


Onitama_exec: Onitama_out
	cd ProjetOnitama/out/production/ProjetOnitama && java Onitama

init_doc:
	doxygen -g

doc:
	doxygen ./ProjetOnitama/Docu/Doxyfile

jar_create:Onitama_out
	jar cfe Onitama.jar Onitama -C ProjetOnitama/out/production/ProjetOnitama .

jar_lunch:
	java -jar Onitama.jar

clean_jar:
	rm -f Onitama.jar

clean_docu:
	rm -rf ./Ressources/Docu/

clean_out:
	rm -rf $(OUT)