.section .text
.global main

main:
    /* Initialisation /
    ldr r0, =symbol32    @ Charger l'adresse de symbol32 dans r0
    ldr r1, [r0]         @ Charger la valeur 32 bits de symbol32 dans r1

    ldrb r3, [r0]        @ Charger la valeur 8 bits de symbol8 dans r3
    mov r4, #1           @ Initialiser r4 à 1 (compteur de boucle)

    / Stocker une valeur 32 bits dans symbol32 /
    add r1, r1, #0x10    @ Ajouter 16 à la valeur dans r1
    str r1, [r0]         @ Stocker la valeur de r1 dans symbol32 (32 bits)

    / Charger la valeur stockée dans symbol32 pour vérification /
    ldr r5, [r0]         @ Charger la valeur de symbol32 dans r5 pour vérification

    / Stocker une valeur 8 bits dans symbol8 /
    add r3, r3, #0x04    @ Ajouter 4 à la valeur dans r3
    strb r3, [r0]        @ Stocker la valeur de r3 dans symbol8 (8 bits)

    / Charger la valeur stockée dans symbol8 pour vérification /
    ldr r6, [r0]        @ Charger la valeur de symbol8 dans r6 pour vérification


end_program:
    / Terminer le programme */
    swi 0x123456          @ Appel système pour terminer le programme

.section .data
.align 2
symbol32:
    .word 0x12345678     @ Donnée 32 bits initiale

.align 2
symbol8:
    .byte 0x12           @ Donnée 8 bits initiale