#!/bin/sh

output=$1

# Démarrer le simulateur ARM en arrière-plan
./arm_simulator --gdb-port 6667 --trace-registers --trace-memory --trace-state SVC &

# Attendre une seconde pour s'assurer que le simulateur est prêt
sleep 1

# Exécuter ARM_elf avec le fichier fourni en argument
./ARM_elf --debug ARM_elf.c --host localhost --service 6667 "$output"
