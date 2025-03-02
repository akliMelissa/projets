.section .melissa
rodata1:
    .word 0x11111111
    .asciz "String in rodata1"

.section .mariem
rodata2:
    .word 0x22222222
    .asciz "String in rodata2"

.section .ilyes
data1:
    .word 0x33333333
    .word rodata1
    .word text1

.section .taki
data2:
    .word 0x44444444
    .word rodata2
    .word text2

.section .data3
data3:
    .word 0x55555555
    .word text3
    .word rodata1

.section .text1
.global text1
text1:
    ldr r0, =rodata1
    ldr r1, =data1
    bx lr

.section .text2
.global text2
text2:
    ldr r0, =rodata2
    ldr r1, =data2
    bx lr

.section .text3
.global text3
text3:
    ldr r0, =data3
    ldr r1, =rodata2
    bx lr

.section .textmain
.global main
main:
    push {lr}
    bl text1
    bl text2
    bl text3
    ldr r0, =data1
    pop {pc}

.section .got
.word data1
.word data2
.word data3
.word rodata1
.word rodata2

.section .init
.global _init
_init:
    ldr r0, =data1
    ldr r1, =rodata1
    bx lr

.section .fini
.global _fini
_fini:
    ldr r0, =data3
    ldr r1, =rodata2
    bx lr