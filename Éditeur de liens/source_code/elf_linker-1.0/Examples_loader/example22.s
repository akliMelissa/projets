.section .text
.global main

main:
    /* Initialisation */
    ldr r0, =symbol32    @ Charger l'adresse de symbol32 dans r0
    ldr r1, [r0]         @ Charger la valeur 32 bits de symbol32 dans r1
    ldr r2, =symbol8     @ Charger l'adresse de symbol8 dans r2
    ldrb r3, [r2]        @ Charger la valeur 8 bits de symbol8 dans r3
    mov r4, #0           @ Initialiser r4 à 0 (compteur de boucle)

loop_start:
    add r4, r4, #1       @ Incrémenter r4 de 1
    cmp r4, #5           @ Comparer r4 à 5
    beq loop_end         @ Si r4 == 5, sauter à loop_end

    add r1, r1, r3       @ Ajouter r3 à r1 (accumuler les valeurs)
    b loop_start         @ Sauter au début de la boucle (branchement non conditionnel)

loop_end:
    /* Branchement conditionnel */
    cmp r1, #100         @ Comparer r1 à 100
    blt below_100        @ Si r1 < 100, sauter à below_100
    mov r5, #1           @ Si r1 >= 100, mettre r5 à 1
    b end_program        @ Aller à la fin du programme

below_100:
    mov r5, #0           @ Si r1 < 100, mettre r5 à 0

end_program:
    /* Terminer le programme */
    swi 0x123456

.section .data
.align 2
symbol32:
    .word 0x12345678     @ Donnée 32 bits initiale

.align 2
symbol8:
    .byte 0x12           @ Donnée 8 bits initiale