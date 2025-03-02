.section .text
.global _start

_start:
    mov r0, #42        @ Charger la valeur 42 dans le registre r0
    mov r1, #1         @ Charger la valeur 1 dans le registre r1
    add r2, r0, r1     @ Additionner les valeurs dans r0 et r1, stocker le résultat dans r2
    b _exit            @ Aller à l'étiquette _exit

_exit:
    mov r7, #1         @ Charger le code de sortie (sys_exit) dans r7
    svc 0              @ Exécuter un appel système pour quitter
