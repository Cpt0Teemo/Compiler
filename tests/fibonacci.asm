# A compiled version of fibonacci.c that has not yet gone through the register allocator.

.data
label_1_staticString:
.asciiz "First "
.align 2
label_2_staticString:
.asciiz " terms of Fibonacci series are : "
.align 2
label_7_staticString:
.asciiz " "
.align 2

.text
jal main

.text
.globl main
main:
addu $fp,$sp,$zero
addi $sp,$sp,-28
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,5
syscall
movz v5,$v0,$zero
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi v0,v5,0
li v6,0
addi v1,v6,0
li v7,1
addi v2,v7,0
la v8,label_1_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v8,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,1
addu $a0,v0,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v11,label_2_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v11,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
li v13,0
addi v3,v13,0
label_3_while_start:
slt v14,v3,v0
beqz v14,label_4_while_end
li v16,1
slt v15,v16,v3
xori v15,v15,1
beqz v15,label_5_true_end
addi v4,v3,0
b label_6_if_end
label_5_true_end:
addu v17,v1,v2
addi v4,v17,0
addi v1,v2,0
addi v2,v4,0
label_6_if_end:
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,1
addu $a0,v4,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v19,label_7_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v19,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
li v22,1
addu v21,v3,v22
addi v3,v21,0
b label_3_while_start
label_4_while_end:
addi $sp,$fp,0
li $v0,10
syscall
