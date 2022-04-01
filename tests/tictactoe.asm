# A compiled version of tictactoe.c that has not yet gone through the register allocator.

.data
label_1_a11:
.space 4
label_2_a12:
.space 4
label_3_a13:
.space 4
label_4_a21:
.space 4
label_5_a22:
.space 4
label_6_a23:
.space 4
label_7_a31:
.space 4
label_8_a32:
.space 4
label_9_a33:
.space 4
label_10_empty:
.space 4
label_77_staticString:
.asciiz "\n"
.align 2
label_78_staticString:
.asciiz "     1   2   3\n"
.align 2
label_79_staticString:
.asciiz "   +---+---+---+\n"
.align 2
label_80_staticString:
.asciiz "a  | "
.align 2
label_81_staticString:
.asciiz " | "
.align 2
label_82_staticString:
.asciiz " |\n"
.align 2
label_83_staticString:
.asciiz "b  | "
.align 2
label_84_staticString:
.asciiz "c  | "
.align 2
label_86_staticString:
.asciiz "Player "
.align 2
label_87_staticString:
.asciiz " has won!\n"
.align 2
label_97_staticString:
.asciiz " select move (e.g. a2)>"
.align 2
label_100_staticString:
.asciiz "That is not a valid move!\n"
.align 2
label_103_staticString:
.asciiz "That move is not possible!\n"
.align 2
label_153_staticString:
.asciiz "It's a draw!\n"
.align 2
label_156_staticString:
.asciiz "Play again? (y/n)> "
.align 2

.text
jal main

.text
reset:
addu $fp,$sp,$zero
la v0,label_1_a11
la v2,label_10_empty
lw v1,0(v2)
sw v1,0(v0)
la v3,label_2_a12
la v5,label_10_empty
lw v4,0(v5)
sw v4,0(v3)
la v6,label_3_a13
la v8,label_10_empty
lw v7,0(v8)
sw v7,0(v6)
la v9,label_4_a21
la v11,label_10_empty
lw v10,0(v11)
sw v10,0(v9)
la v12,label_5_a22
la v14,label_10_empty
lw v13,0(v14)
sw v13,0(v12)
la v15,label_6_a23
la v17,label_10_empty
lw v16,0(v17)
sw v16,0(v15)
la v18,label_7_a31
la v20,label_10_empty
lw v19,0(v20)
sw v19,0(v18)
la v21,label_8_a32
la v23,label_10_empty
lw v22,0(v23)
sw v22,0(v21)
la v24,label_9_a33
la v26,label_10_empty
lw v25,0(v26)
sw v25,0(v24)
addi $sp,$fp,0
jr $ra

.text
full:
addu $fp,$sp,$zero
addi $sp,$sp,-4
li v28,0
addi v27,v28,0
la v31,label_1_a11
lw v30,0(v31)
la v33,label_10_empty
lw v32,0(v33)
xor v29,v30,v32
sltu v29,$zero,v29
beqz v29,label_13_true_end
li v35,1
addu v34,v27,v35
addi v27,v34,0
b label_14_if_end
label_13_true_end:
label_14_if_end:
la v38,label_4_a21
lw v37,0(v38)
la v40,label_10_empty
lw v39,0(v40)
xor v36,v37,v39
sltu v36,$zero,v36
beqz v36,label_15_true_end
li v42,1
addu v41,v27,v42
addi v27,v41,0
b label_16_if_end
label_15_true_end:
label_16_if_end:
la v45,label_7_a31
lw v44,0(v45)
la v47,label_10_empty
lw v46,0(v47)
xor v43,v44,v46
sltu v43,$zero,v43
beqz v43,label_17_true_end
li v49,1
addu v48,v27,v49
addi v27,v48,0
b label_18_if_end
label_17_true_end:
label_18_if_end:
la v52,label_2_a12
lw v51,0(v52)
la v54,label_10_empty
lw v53,0(v54)
xor v50,v51,v53
sltu v50,$zero,v50
beqz v50,label_19_true_end
li v56,1
addu v55,v27,v56
addi v27,v55,0
b label_20_if_end
label_19_true_end:
label_20_if_end:
la v59,label_5_a22
lw v58,0(v59)
la v61,label_10_empty
lw v60,0(v61)
xor v57,v58,v60
sltu v57,$zero,v57
beqz v57,label_21_true_end
li v63,1
addu v62,v27,v63
addi v27,v62,0
b label_22_if_end
label_21_true_end:
label_22_if_end:
la v66,label_8_a32
lw v65,0(v66)
la v68,label_10_empty
lw v67,0(v68)
xor v64,v65,v67
sltu v64,$zero,v64
beqz v64,label_23_true_end
li v70,1
addu v69,v27,v70
addi v27,v69,0
b label_24_if_end
label_23_true_end:
label_24_if_end:
la v73,label_3_a13
lw v72,0(v73)
la v75,label_10_empty
lw v74,0(v75)
xor v71,v72,v74
sltu v71,$zero,v71
beqz v71,label_25_true_end
li v77,1
addu v76,v27,v77
addi v27,v76,0
b label_26_if_end
label_25_true_end:
label_26_if_end:
la v80,label_6_a23
lw v79,0(v80)
la v82,label_10_empty
lw v81,0(v82)
xor v78,v79,v81
sltu v78,$zero,v78
beqz v78,label_27_true_end
li v84,1
addu v83,v27,v84
addi v27,v83,0
b label_28_if_end
label_27_true_end:
label_28_if_end:
la v87,label_9_a33
lw v86,0(v87)
la v89,label_10_empty
lw v88,0(v89)
xor v85,v86,v88
sltu v85,$zero,v85
beqz v85,label_29_true_end
li v91,1
addu v90,v27,v91
addi v27,v90,0
b label_30_if_end
label_29_true_end:
label_30_if_end:
li v93,9
xor v92,v27,v93
sltiu v92,v92,1
beqz v92,label_31_true_end
li v94,1
sw v94,0($fp)
jr $ra
b label_32_if_end
label_31_true_end:
li v95,0
sw v95,0($fp)
jr $ra
label_32_if_end:
addi $sp,$fp,0
jr $ra

.text
set:
addu $fp,$sp,$zero
addi $sp,$sp,-4
li v97,1
addi v96,v97,0
addi v100,$fp,4
lw v99,0(v100)
li v101,97
xor v98,v99,v101
sltiu v98,v98,1
beqz v98,label_34_true_end
addi v104,$fp,8
lw v103,0(v104)
li v105,1
xor v102,v103,v105
sltiu v102,v102,1
beqz v102,label_36_true_end
la v108,label_1_a11
lw v107,0(v108)
la v110,label_10_empty
lw v109,0(v110)
xor v106,v107,v109
sltiu v106,v106,1
beqz v106,label_38_true_end
la v111,label_1_a11
addi v113,$fp,12
lw v112,0(v113)
sw v112,0(v111)
b label_39_if_end
label_38_true_end:
li v115,0
li v116,1
sub v114,v115,v116
addi v96,v114,0
label_39_if_end:
b label_37_if_end
label_36_true_end:
addi v119,$fp,8
lw v118,0(v119)
li v120,2
xor v117,v118,v120
sltiu v117,v117,1
beqz v117,label_40_true_end
la v123,label_2_a12
lw v122,0(v123)
la v125,label_10_empty
lw v124,0(v125)
xor v121,v122,v124
sltiu v121,v121,1
beqz v121,label_42_true_end
la v126,label_2_a12
addi v128,$fp,12
lw v127,0(v128)
sw v127,0(v126)
b label_43_if_end
label_42_true_end:
li v130,0
li v131,1
sub v129,v130,v131
addi v96,v129,0
label_43_if_end:
b label_41_if_end
label_40_true_end:
addi v134,$fp,8
lw v133,0(v134)
li v135,3
xor v132,v133,v135
sltiu v132,v132,1
beqz v132,label_44_true_end
la v138,label_3_a13
lw v137,0(v138)
la v140,label_10_empty
lw v139,0(v140)
xor v136,v137,v139
sltiu v136,v136,1
beqz v136,label_46_true_end
la v141,label_3_a13
addi v143,$fp,12
lw v142,0(v143)
sw v142,0(v141)
b label_47_if_end
label_46_true_end:
li v145,0
li v146,1
sub v144,v145,v146
addi v96,v144,0
label_47_if_end:
b label_45_if_end
label_44_true_end:
li v147,0
addi v96,v147,0
label_45_if_end:
label_41_if_end:
label_37_if_end:
b label_35_if_end
label_34_true_end:
addi v150,$fp,4
lw v149,0(v150)
li v151,98
xor v148,v149,v151
sltiu v148,v148,1
beqz v148,label_48_true_end
addi v154,$fp,8
lw v153,0(v154)
li v155,1
xor v152,v153,v155
sltiu v152,v152,1
beqz v152,label_50_true_end
la v158,label_4_a21
lw v157,0(v158)
la v160,label_10_empty
lw v159,0(v160)
xor v156,v157,v159
sltiu v156,v156,1
beqz v156,label_52_true_end
la v161,label_4_a21
addi v163,$fp,12
lw v162,0(v163)
sw v162,0(v161)
b label_53_if_end
label_52_true_end:
li v165,0
li v166,1
sub v164,v165,v166
addi v96,v164,0
label_53_if_end:
b label_51_if_end
label_50_true_end:
addi v169,$fp,8
lw v168,0(v169)
li v170,2
xor v167,v168,v170
sltiu v167,v167,1
beqz v167,label_54_true_end
la v173,label_5_a22
lw v172,0(v173)
la v175,label_10_empty
lw v174,0(v175)
xor v171,v172,v174
sltiu v171,v171,1
beqz v171,label_56_true_end
la v176,label_5_a22
addi v178,$fp,12
lw v177,0(v178)
sw v177,0(v176)
b label_57_if_end
label_56_true_end:
li v180,0
li v181,1
sub v179,v180,v181
addi v96,v179,0
label_57_if_end:
b label_55_if_end
label_54_true_end:
addi v184,$fp,8
lw v183,0(v184)
li v185,3
xor v182,v183,v185
sltiu v182,v182,1
beqz v182,label_58_true_end
la v188,label_6_a23
lw v187,0(v188)
la v190,label_10_empty
lw v189,0(v190)
xor v186,v187,v189
sltiu v186,v186,1
beqz v186,label_60_true_end
la v191,label_6_a23
addi v193,$fp,12
lw v192,0(v193)
sw v192,0(v191)
b label_61_if_end
label_60_true_end:
li v195,0
li v196,1
sub v194,v195,v196
addi v96,v194,0
label_61_if_end:
b label_59_if_end
label_58_true_end:
li v197,0
addi v96,v197,0
label_59_if_end:
label_55_if_end:
label_51_if_end:
b label_49_if_end
label_48_true_end:
addi v200,$fp,4
lw v199,0(v200)
li v201,99
xor v198,v199,v201
sltiu v198,v198,1
beqz v198,label_62_true_end
addi v204,$fp,8
lw v203,0(v204)
li v205,1
xor v202,v203,v205
sltiu v202,v202,1
beqz v202,label_64_true_end
la v208,label_7_a31
lw v207,0(v208)
la v210,label_10_empty
lw v209,0(v210)
xor v206,v207,v209
sltiu v206,v206,1
beqz v206,label_66_true_end
la v211,label_7_a31
addi v213,$fp,12
lw v212,0(v213)
sw v212,0(v211)
b label_67_if_end
label_66_true_end:
li v215,0
li v216,1
sub v214,v215,v216
addi v96,v214,0
label_67_if_end:
b label_65_if_end
label_64_true_end:
addi v219,$fp,8
lw v218,0(v219)
li v220,2
xor v217,v218,v220
sltiu v217,v217,1
beqz v217,label_68_true_end
la v223,label_8_a32
lw v222,0(v223)
la v225,label_10_empty
lw v224,0(v225)
xor v221,v222,v224
sltiu v221,v221,1
beqz v221,label_70_true_end
la v226,label_8_a32
addi v228,$fp,12
lw v227,0(v228)
sw v227,0(v226)
b label_71_if_end
label_70_true_end:
li v230,0
li v231,1
sub v229,v230,v231
addi v96,v229,0
label_71_if_end:
b label_69_if_end
label_68_true_end:
addi v234,$fp,8
lw v233,0(v234)
li v235,3
xor v232,v233,v235
sltiu v232,v232,1
beqz v232,label_72_true_end
la v238,label_9_a33
lw v237,0(v238)
la v240,label_10_empty
lw v239,0(v240)
xor v236,v237,v239
sltiu v236,v236,1
beqz v236,label_74_true_end
la v241,label_9_a33
addi v243,$fp,12
lw v242,0(v243)
sw v242,0(v241)
b label_75_if_end
label_74_true_end:
li v245,0
li v246,1
sub v244,v245,v246
addi v96,v244,0
label_75_if_end:
b label_73_if_end
label_72_true_end:
li v247,0
addi v96,v247,0
label_73_if_end:
label_69_if_end:
label_65_if_end:
b label_63_if_end
label_62_true_end:
li v248,0
addi v96,v248,0
label_63_if_end:
label_49_if_end:
label_35_if_end:
sw v96,0($fp)
jr $ra
addi $sp,$fp,0
jr $ra

.text
printGame:
addu $fp,$sp,$zero
la v249,label_77_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v249,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v251,label_78_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v251,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v253,label_79_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v253,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v255,label_80_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v255,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v258,label_1_a11
lw v257,0(v258)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,11
addu $a0,v257,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v260,label_81_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v260,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v263,label_2_a12
lw v262,0(v263)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,11
addu $a0,v262,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v265,label_81_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v265,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v268,label_3_a13
lw v267,0(v268)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,11
addu $a0,v267,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v270,label_82_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v270,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v272,label_79_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v272,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v274,label_83_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v274,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v277,label_4_a21
lw v276,0(v277)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,11
addu $a0,v276,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v279,label_81_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v279,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v282,label_5_a22
lw v281,0(v282)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,11
addu $a0,v281,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v284,label_81_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v284,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v287,label_6_a23
lw v286,0(v287)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,11
addu $a0,v286,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v289,label_82_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v289,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v291,label_79_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v291,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v293,label_84_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v293,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v296,label_7_a31
lw v295,0(v296)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,11
addu $a0,v295,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v298,label_81_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v298,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v301,label_8_a32
lw v300,0(v301)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,11
addu $a0,v300,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v303,label_81_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v303,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v306,label_9_a33
lw v305,0(v306)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,11
addu $a0,v305,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v308,label_82_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v308,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v310,label_79_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v310,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v312,label_77_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v312,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi $sp,$fp,0
jr $ra

.text
printWinner:
addu $fp,$sp,$zero
la v314,label_86_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v314,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi v317,$fp,0
lw v316,0(v317)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,1
addu $a0,v316,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v319,label_87_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v319,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi $sp,$fp,0
jr $ra

.text
switchPlayer:
addu $fp,$sp,$zero
addi v323,$fp,4
lw v322,0(v323)
li v324,1
xor v321,v322,v324
sltiu v321,v321,1
beqz v321,label_89_true_end
li v325,2
sw v325,0($fp)
jr $ra
b label_90_if_end
label_89_true_end:
li v326,1
sw v326,0($fp)
jr $ra
label_90_if_end:
addi $sp,$fp,0
jr $ra

.text
get_mark:
addu $fp,$sp,$zero
addi v329,$fp,4
lw v328,0(v329)
li v330,1
xor v327,v328,v330
sltiu v327,v327,1
beqz v327,label_92_true_end
li v331,88
sw v331,0($fp)
jr $ra
b label_93_if_end
label_92_true_end:
li v332,79
sw v332,0($fp)
jr $ra
label_93_if_end:
addi $sp,$fp,0
jr $ra

.text
selectmove:
addu $fp,$sp,$zero
addi $sp,$sp,-36
li v338,1
addi v333,v338,0
label_95_while_start:
beqz v333,label_96_while_end
la v339,label_86_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v339,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi v342,$fp,0
lw v341,0(v342)
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,1
addu $a0,v341,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
la v344,label_97_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v344,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,12
syscall
movz v346,$v0,$zero
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi v334,v346,0
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,5
syscall
movz v347,$v0,$zero
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi v335,v347,0
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
addi $sp,$sp,-4
addi $sp,$sp,-4
addi v349,$fp,0
lw v348,0(v349)
sw v348,4($sp)
jal get_mark
addu $sp,$fp,$zero
lw $fp,8($sp)
lw v350,0($sp)
sw v350,-12($fp)
addi $sp,$sp,4
addi $sp,$sp,4
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
lw v351,-12($fp)
addi v336,v351,0
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
addi $sp,$sp,-12
addi $sp,$sp,-4
sw v334,4($sp)
sw v335,8($sp)
sw v336,12($sp)
jal set
addu $sp,$fp,$zero
lw $fp,16($sp)
lw v352,0($sp)
sw v352,-16($fp)
addi $sp,$sp,4
addi $sp,$sp,12
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
lw v353,-16($fp)
addi v337,v353,0
li v355,0
xor v354,v337,v355
sltiu v354,v354,1
beqz v354,label_98_true_end
la v356,label_100_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v356,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
b label_99_if_end
label_98_true_end:
li v360,0
li v361,1
sub v359,v360,v361
xor v358,v337,v359
sltiu v358,v358,1
beqz v358,label_101_true_end
la v362,label_103_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v362,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
b label_102_if_end
label_101_true_end:
li v364,0
addi v333,v364,0
label_102_if_end:
label_99_if_end:
b label_95_while_start
label_96_while_end:
addi $sp,$fp,0
jr $ra

.text
won:
addu $fp,$sp,$zero
addi $sp,$sp,-4
li v366,0
addi v365,v366,0
la v369,label_1_a11
lw v368,0(v369)
addi v371,$fp,4
lw v370,0(v371)
xor v367,v368,v370
sltiu v367,v367,1
beqz v367,label_105_true_end
la v374,label_4_a21
lw v373,0(v374)
addi v376,$fp,4
lw v375,0(v376)
xor v372,v373,v375
sltiu v372,v372,1
beqz v372,label_107_true_end
la v379,label_7_a31
lw v378,0(v379)
addi v381,$fp,4
lw v380,0(v381)
xor v377,v378,v380
sltiu v377,v377,1
beqz v377,label_109_true_end
li v382,1
addi v365,v382,0
b label_110_if_end
label_109_true_end:
label_110_if_end:
b label_108_if_end
label_107_true_end:
la v385,label_5_a22
lw v384,0(v385)
addi v387,$fp,4
lw v386,0(v387)
xor v383,v384,v386
sltiu v383,v383,1
beqz v383,label_111_true_end
la v390,label_9_a33
lw v389,0(v390)
addi v392,$fp,4
lw v391,0(v392)
xor v388,v389,v391
sltiu v388,v388,1
beqz v388,label_113_true_end
li v393,1
addi v365,v393,0
b label_114_if_end
label_113_true_end:
label_114_if_end:
b label_112_if_end
label_111_true_end:
la v396,label_2_a12
lw v395,0(v396)
addi v398,$fp,4
lw v397,0(v398)
xor v394,v395,v397
sltiu v394,v394,1
beqz v394,label_115_true_end
la v401,label_3_a13
lw v400,0(v401)
addi v403,$fp,4
lw v402,0(v403)
xor v399,v400,v402
sltiu v399,v399,1
beqz v399,label_117_true_end
li v404,1
addi v365,v404,0
b label_118_if_end
label_117_true_end:
label_118_if_end:
b label_116_if_end
label_115_true_end:
label_116_if_end:
label_112_if_end:
label_108_if_end:
b label_106_if_end
label_105_true_end:
label_106_if_end:
la v407,label_2_a12
lw v406,0(v407)
addi v409,$fp,4
lw v408,0(v409)
xor v405,v406,v408
sltiu v405,v405,1
beqz v405,label_119_true_end
la v412,label_5_a22
lw v411,0(v412)
addi v414,$fp,4
lw v413,0(v414)
xor v410,v411,v413
sltiu v410,v410,1
beqz v410,label_121_true_end
la v417,label_8_a32
lw v416,0(v417)
addi v419,$fp,4
lw v418,0(v419)
xor v415,v416,v418
sltiu v415,v415,1
beqz v415,label_123_true_end
li v420,1
addi v365,v420,0
b label_124_if_end
label_123_true_end:
label_124_if_end:
b label_122_if_end
label_121_true_end:
label_122_if_end:
b label_120_if_end
label_119_true_end:
label_120_if_end:
la v423,label_3_a13
lw v422,0(v423)
addi v425,$fp,4
lw v424,0(v425)
xor v421,v422,v424
sltiu v421,v421,1
beqz v421,label_125_true_end
la v428,label_6_a23
lw v427,0(v428)
addi v430,$fp,4
lw v429,0(v430)
xor v426,v427,v429
sltiu v426,v426,1
beqz v426,label_127_true_end
la v433,label_9_a33
lw v432,0(v433)
addi v435,$fp,4
lw v434,0(v435)
xor v431,v432,v434
sltiu v431,v431,1
beqz v431,label_129_true_end
li v436,1
addi v365,v436,0
b label_130_if_end
label_129_true_end:
label_130_if_end:
b label_128_if_end
label_127_true_end:
la v439,label_5_a22
lw v438,0(v439)
addi v441,$fp,4
lw v440,0(v441)
xor v437,v438,v440
sltiu v437,v437,1
beqz v437,label_131_true_end
la v444,label_7_a31
lw v443,0(v444)
addi v446,$fp,4
lw v445,0(v446)
xor v442,v443,v445
sltiu v442,v442,1
beqz v442,label_133_true_end
li v447,1
addi v365,v447,0
b label_134_if_end
label_133_true_end:
label_134_if_end:
b label_132_if_end
label_131_true_end:
label_132_if_end:
label_128_if_end:
b label_126_if_end
label_125_true_end:
label_126_if_end:
la v450,label_4_a21
lw v449,0(v450)
addi v452,$fp,4
lw v451,0(v452)
xor v448,v449,v451
sltiu v448,v448,1
beqz v448,label_135_true_end
la v455,label_5_a22
lw v454,0(v455)
addi v457,$fp,4
lw v456,0(v457)
xor v453,v454,v456
sltiu v453,v453,1
beqz v453,label_137_true_end
la v460,label_6_a23
lw v459,0(v460)
addi v462,$fp,4
lw v461,0(v462)
xor v458,v459,v461
sltiu v458,v458,1
beqz v458,label_139_true_end
li v463,1
addi v365,v463,0
b label_140_if_end
label_139_true_end:
label_140_if_end:
b label_138_if_end
label_137_true_end:
label_138_if_end:
b label_136_if_end
label_135_true_end:
label_136_if_end:
la v466,label_7_a31
lw v465,0(v466)
addi v468,$fp,4
lw v467,0(v468)
xor v464,v465,v467
sltiu v464,v464,1
beqz v464,label_141_true_end
la v471,label_8_a32
lw v470,0(v471)
addi v473,$fp,4
lw v472,0(v473)
xor v469,v470,v472
sltiu v469,v469,1
beqz v469,label_143_true_end
la v476,label_9_a33
lw v475,0(v476)
addi v478,$fp,4
lw v477,0(v478)
xor v474,v475,v477
sltiu v474,v474,1
beqz v474,label_145_true_end
li v479,1
addi v365,v479,0
b label_146_if_end
label_145_true_end:
label_146_if_end:
b label_144_if_end
label_143_true_end:
label_144_if_end:
b label_142_if_end
label_141_true_end:
label_142_if_end:
sw v365,0($fp)
jr $ra
addi $sp,$fp,0
jr $ra

.text
.globl main
main:
addu $fp,$sp,$zero
addi $sp,$sp,-36
la v484,label_10_empty
li v485,32
sw v485,0(v484)
li v486,1
addi v480,v486,0
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
jal reset
addu $sp,$fp,$zero
lw $fp,0($sp)
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
jal printGame
addu $sp,$fp,$zero
lw $fp,0($sp)
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
li v489,1
addi v481,v489,0
label_147_while_start:
beqz v480,label_148_while_end
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
addi $sp,$sp,-4
sw v481,0($sp)
jal selectmove
addu $sp,$fp,$zero
lw $fp,4($sp)
addi $sp,$sp,4
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
addi $sp,$sp,-4
addi $sp,$sp,-4
sw v481,4($sp)
jal get_mark
addu $sp,$fp,$zero
lw $fp,8($sp)
lw v491,0($sp)
sw v491,-4($fp)
addi $sp,$sp,4
addi $sp,$sp,4
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
lw v492,-4($fp)
addi v482,v492,0
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
jal printGame
addu $sp,$fp,$zero
lw $fp,0($sp)
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
addi $sp,$sp,-4
addi $sp,$sp,-4
sw v482,4($sp)
jal won
addu $sp,$fp,$zero
lw $fp,8($sp)
lw v494,0($sp)
sw v494,0($fp)
addi $sp,$sp,4
addi $sp,$sp,4
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
lw v495,0($fp)
beqz v495,label_149_true_end
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
addi $sp,$sp,-4
sw v481,0($sp)
jal printWinner
addu $sp,$fp,$zero
lw $fp,4($sp)
addi $sp,$sp,4
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
li v497,0
addi v480,v497,0
b label_150_if_end
label_149_true_end:
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
addi $sp,$sp,-4
jal full
addu $sp,$fp,$zero
lw $fp,4($sp)
lw v499,0($sp)
sw v499,0($fp)
addi $sp,$sp,4
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
lw v500,0($fp)
li v501,1
xor v498,v500,v501
sltiu v498,v498,1
beqz v498,label_151_true_end
la v502,label_153_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v502,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
li v504,0
addi v480,v504,0
b label_152_if_end
label_151_true_end:
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
addi $sp,$sp,-4
addi $sp,$sp,-4
sw v481,4($sp)
jal switchPlayer
addu $sp,$fp,$zero
lw $fp,8($sp)
lw v505,0($sp)
sw v505,-8($fp)
addi $sp,$sp,4
addi $sp,$sp,4
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
lw v506,-8($fp)
addi v481,v506,0
label_152_if_end:
label_150_if_end:
li v508,0
xor v507,v480,v508
sltiu v507,v507,1
beqz v507,label_154_true_end
la v509,label_156_staticString
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,4
addu $a0,v509,$zero
syscall
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,12
syscall
movz v511,$v0,$zero
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
addi v483,v511,0
li v513,121
xor v512,v483,v513
sltiu v512,v512,1
beqz v512,label_157_true_end
li v514,1
addi v480,v514,0
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,12
syscall
movz v515,$v0,$zero
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
jal reset
addu $sp,$fp,$zero
lw $fp,0($sp)
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
b label_158_if_end
label_157_true_end:
li v518,89
xor v517,v483,v518
sltiu v517,v517,1
beqz v517,label_159_true_end
li v519,1
addi v480,v519,0
addi $sp,$sp,-4
sw $v0,0($sp)
addi $sp,$sp,-4
sw $a0,0($sp)
li $v0,12
syscall
movz v520,$v0,$zero
lw $v0,0($sp)
lw $a0,4($sp)
addi $sp,$sp,8
pushRegisters
addi $sp,$sp,-4
sw $ra,0($sp)
addi $sp,$sp,-4
sw $fp,0($sp)
jal reset
addu $sp,$fp,$zero
lw $fp,0($sp)
lw $fp,0($sp)
addi $sp,$sp,4
lw $ra,0($sp)
addi $sp,$sp,4
popRegisters
b label_160_if_end
label_159_true_end:
label_160_if_end:
label_158_if_end:
b label_155_if_end
label_154_true_end:
label_155_if_end:
b label_147_while_start
label_148_while_end:
addi $sp,$fp,0
li $v0,10
syscall

