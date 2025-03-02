.section .amine
const1: .word 0x11111111
const2: .word 0x22222222
string1: .asciz "Hello"
string2: .asciz "World"

.section .data
var1: .word 0x33333333
ptr1: .word const1
ptr2: .word string1

.section .text1
.global func1
func1:
    ldr r0, =const1
    ldr r1, =string1
    bx lr

.section .text2
.global func2
func2:
    ldr r0, =const2
    ldr r1, =string2
    bx lr

.section .amdjed
var2: .word 0x44444444
ptr3: .word const2
ptr4: .word string2

.text
.global main
main:
    push {lr}
    bl func1
    bl func2
    ldr r0, =var1
    ldr r1, =var2
    ldr r2, =ptr1
    ldr r3, =ptr2
    pop {lr}
    bx lr