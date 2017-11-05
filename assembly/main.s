.intel_syntax noprefix
.text
	.global main
main:
	push ebp
	mov ebp, esp
	call main0
	mov eax, 0
	mov esp,ebp
	pop ebp
	ret
puti0:
	push ebp
	mov ebp, esp
	push edi
	push ebx
	push esi
	mov esi, DWORD PTR [ebp+16]
	push esi
	mov eax, OFFSET FLAT:printf_int
	push eax
	call printf
	add esp, 8
	pop esi
	pop ebx
	pop edi
	mov esp,ebp
	pop ebp
	ret
putc0:
	push ebp
	mov ebp, esp
	push edi
	push ebx
	push esi
 mov esi, DWORD PTR [ebp+16]
 push esi
	mov eax, OFFSET FLAT:printf_char
	push eax
	call printf
	add esp, 8
	pop esi
	pop ebx
	pop edi
	mov esp,ebp
	pop ebp
	ret
puts0:
	push ebp
	mov ebp, esp
	push edi
	push ebx
	push esi
 mov esi, DWORD PTR [ebp+16]
 push esi
	mov eax, OFFSET FLAT:printf_string
	push eax
	call printf
	add esp, 8
	pop esi
	pop ebx
	pop edi
	mov esp,ebp
	pop ebp
	ret
geti0:
	push ebp
	mov ebp, esp
	push edi
	push ebx
	push esi
	mov eax, OFFSET FLAT:prompt_int
	push eax
	call printf
	add esp, 4
 lea eax, [ebp+12]
 push eax
	mov eax, OFFSET FLAT:int_fmt
	push eax
	call scanf
	add esp, 8
 mov eax, DWORD PTR [ebp+12]
	pop esi
	pop ebx
	pop edi
	mov esp,ebp
	pop ebp
	ret
getc0:
	push ebp
	mov ebp, esp
	push edi
	push ebx
	push esi
	mov eax, OFFSET FLAT:prompt_char
	push eax
	call printf
	add esp, 4
	push DWORD PTR [ebp+12]
	mov eax, OFFSET FLAT:char_fmt
	push eax
	call scanf
	add esp, 8
 mov eax, DWORD PTR [ebp+12]
	pop esi
	pop ebx
	pop edi
	mov esp,ebp
	pop ebp
	ret
gets0:
	push ebp
	push edi
	push ebx
	push esi
	mov eax, DWORD PTR stdin
	push eax
 push DWORD PTR [ebp+16]
 push DWORD PTR [ebp+20]
	call fgets
	add esp, 12
 push DWORD PTR [ebp+12]
	call strchr
	add esp, 8
	cmp eax, 0
	je grace_gets_no_newline
	mov BYTE PTR [eax], 0
grace_gets_no_newline:
	pop esi
	pop ebx
	pop edi
	mov esp,ebp
	pop ebp
	ret
abs0:
	push ebp
	mov ebp, esp
	push edi
	push ebx
	push esi
	mov eax, DWORD PTR [ebp+16]
	mov edx, 0
	cmp eax, edx
	jl abs_neg
	jmp abs_pos
abs_neg:
	neg eax
	mov esi, DWORD PTR [ebp+12]
	mov DWORD PTR [esi], eax
	pop esi
	pop ebx
	pop edi
	mov esp,ebp
	pop ebp
	ret
abs_pos:
	mov esi, DWORD PTR [ebp+12]
	mov DWORD PTR [esi], eax
	pop esi
	pop ebx
	pop edi
	mov esp,ebp
	pop ebp
	ret
ord0:
	push ebp
	mov ebp, esp
	push edi
	push ebx
	push esi
	mov ebx, DWORD PTR [ebx+16]
	mov esi, DWORD PTR [ebp+12]
	mov DWORD PTR [esi], ebx
	pop esi
	pop ebx
	pop edi
	mov esp,ebp
	pop ebp
	ret
chr0:
	push ebp
	mov ebp, esp
	push edi
	push ebx
	push esi
	mov ebx, DWORD PTR [ebx+16]
	mov esi, DWORD PTR [ebp+12]
	mov DWORD PTR [esi], ebx
	pop esi
	pop ebx
	pop edi
	mov esp,ebp
	pop ebp
	ret
# 1:unit,swap,-,-
instruction_label2:
swap2:
	push ebp
 mov ebp, esp
	sub esp, 4
	push edi
	push ebx
	push esi

# 2::=,x,-,t
instruction_label3:
	mov esi, DWORD PTR [ebp+16]
	mov ecx, DWORD PTR [esi]
	mov DWORD PTR [ebp-4], ecx

# 3::=,y,-,x
instruction_label4:
	mov esi, DWORD PTR [ebp+20]
	mov ecx, DWORD PTR [esi]
	mov esi, DWORD PTR [ebp+16]
	mov DWORD PTR [esi], ecx

# 4::=,t,-,y
instruction_label5:
	mov ecx, DWORD PTR [ebp-4]
	mov esi, DWORD PTR [ebp+20]
	mov DWORD PTR [esi], ecx

# 5:endu,swap,-,-
instruction_label6:
	pop esi
	pop ebx
	pop edi
	add esp, 4
 mov esp, ebp
	pop ebp
	ret

# 6:unit,bsort,-,-
instruction_label7:
bsort1:
	push ebp
 mov ebp, esp
	sub esp, 40
	push edi
	push ebx
	push esi

# 7::=,1,-,changed
instruction_label8:
	mov ecx, 1
	mov DWORD PTR [ebp-4], ecx

# 8:>,changed,0,11
instruction_label9:
	mov eax, DWORD PTR [ebp-4]
	mov edx, 0
	cmp eax, edx
	jg instruction_label11

# 9:jump,-,-,32
instruction_label10:
	jmp instruction_label32

# 10::=,0,-,changed
instruction_label11:
	mov ecx, 0
	mov DWORD PTR [ebp-4], ecx

# 11::=,0,-,i
instruction_label12:
	mov ecx, 0
	mov DWORD PTR [ebp-8], ecx

# 12:-,1,n,$1
instruction_label13:
	mov eax, DWORD PTR [ebp+16]
	mov edx, 1
	sub eax, edx
	mov DWORD PTR [ebp-12], eax

# 13:<,i,$1,16
instruction_label14:
	mov eax, DWORD PTR [ebp-8]
	mov edx, DWORD PTR [ebp-12]
	cmp eax, edx
	jl instruction_label16

# 14:jump,-,-,31
instruction_label15:
	jmp instruction_label31

# 15:array,x,i,$2
instruction_label16:
	mov eax, DWORD PTR [ebp-8]
	mov ecx, 4
	imul ecx
	mov esi, DWORD PTR [ebp+20]
	lea ecx, [esi]
	add eax, ecx
	mov DWORD PTR [ebp-16], eax

# 16:+,1,i,$3
instruction_label17:
	mov eax, DWORD PTR [ebp-8]
	mov edx, 1
	add eax, edx
	mov DWORD PTR [ebp-20], eax

# 17:array,x,$3,$4
instruction_label18:
	mov eax, DWORD PTR [ebp-20]
	mov ecx, 4
	imul ecx
	mov esi, DWORD PTR [ebp+20]
	lea ecx, [esi]
	add eax, ecx
	mov DWORD PTR [ebp-24], eax

# 18:>,[$2],[$4],21
instruction_label19:
	mov edi, DWORD PTR [ebp-16]
	mov eax, DWORD PTR [edi]
	mov edi, DWORD PTR [ebp-24]
	mov edx, DWORD PTR [edi]
	cmp eax, edx
	jg instruction_label21

# 19:jump,-,-,28
instruction_label20:
	jmp instruction_label28

# 20:array,x,i,$5
instruction_label21:
	mov eax, DWORD PTR [ebp-8]
	mov ecx, 4
	imul ecx
	mov esi, DWORD PTR [ebp+20]
	lea ecx, [esi]
	add eax, ecx
	mov DWORD PTR [ebp-28], eax

# 21:+,1,i,$6
instruction_label22:
	mov eax, DWORD PTR [ebp-8]
	mov edx, 1
	add eax, edx
	mov DWORD PTR [ebp-32], eax

# 22:array,x,$6,$7
instruction_label23:
	mov eax, DWORD PTR [ebp-32]
	mov ecx, 4
	imul ecx
	mov esi, DWORD PTR [ebp+20]
	lea ecx, [esi]
	add eax, ecx
	mov DWORD PTR [ebp-36], eax

# 24:par,[$7],R,-
instruction_label25:
	mov esi, DWORD PTR [ebp-36]
	push esi

# 23:par,[$5],R,-
instruction_label24:
	mov esi, DWORD PTR [ebp-28]
	push esi

# 25:call,-,-,swap
instruction_label26:
	sub esp, 4
	push ebp
	call swap2
	add esp, 16

# 26::=,1,-,changed
instruction_label27:
	mov ecx, 1
	mov DWORD PTR [ebp-4], ecx

# 27:+,1,i,$8
instruction_label28:
	mov eax, DWORD PTR [ebp-8]
	mov edx, 1
	add eax, edx
	mov DWORD PTR [ebp-40], eax

# 28::=,$8,-,i
instruction_label29:
	mov ecx, DWORD PTR [ebp-40]
	mov DWORD PTR [ebp-8], ecx

# 29:jump,-,-,13
instruction_label30:
	jmp instruction_label13

# 30:jump,-,-,9
instruction_label31:
	jmp instruction_label9

# 31:endu,bsort,-,-
instruction_label32:
	pop esi
	pop ebx
	pop edi
	add esp, 40
 mov esp, ebp
	pop ebp
	ret

# 32:unit,putArray,-,-
instruction_label33:
putArray1:
	push ebp
 mov ebp, esp
	sub esp, 12
	push edi
	push ebx
	push esi

# 33::=,0,-,i
instruction_label34:
	mov ecx, 0
	mov DWORD PTR [ebp-4], ecx

# 34:<,i,n,37
instruction_label35:
	mov eax, DWORD PTR [ebp-4]
	mov edx, DWORD PTR [ebp+16]
	cmp eax, edx
	jl instruction_label37

# 35:jump,-,-,43
instruction_label36:
	jmp instruction_label43

# 36:array,x,i,$9
instruction_label37:
	mov eax, DWORD PTR [ebp-4]
	mov ecx, 4
	imul ecx
	mov esi, DWORD PTR [ebp+20]
	lea ecx, [esi]
	add eax, ecx
	mov DWORD PTR [ebp-8], eax

# 37:par,[$9],V,-
instruction_label38:
	mov edi, DWORD PTR [ebp-8]
	mov eax, DWORD PTR [edi]
	push eax

# 38:call,-,-,puti
instruction_label39:
	sub esp, 4
	mov esi,DWORD PTR [ebp+8]
	push esi
	call puti0
	add esp, 12

# 39:+,1,i,$10
instruction_label40:
	mov eax, DWORD PTR [ebp-4]
	mov edx, 1
	add eax, edx
	mov DWORD PTR [ebp-12], eax

# 40::=,$10,-,i
instruction_label41:
	mov ecx, DWORD PTR [ebp-12]
	mov DWORD PTR [ebp-4], ecx

# 41:jump,-,-,35
instruction_label42:
	jmp instruction_label35

# 42:endu,putArray,-,-
instruction_label43:
	pop esi
	pop ebx
	pop edi
	add esp, 12
 mov esp, ebp
	pop ebp
	ret

# 43:unit,main,-,-
instruction_label44:
main0:
	push ebp
 mov ebp, esp
	sub esp, 100
	push edi
	push ebx
	push esi

# 44::=,65,-,seed
instruction_label45:
	mov ecx, 65
	mov DWORD PTR [ebp-4], ecx

# 45::=,0,-,i
instruction_label46:
	mov ecx, 0
	mov DWORD PTR [ebp-8], ecx

# 46:<,i,16,49
instruction_label47:
	mov eax, DWORD PTR [ebp-8]
	mov edx, 16
	cmp eax, edx
	jl instruction_label49

# 47:jump,-,-,59
instruction_label48:
	jmp instruction_label59

# 48:*,137,seed,$11
instruction_label49:
	mov eax, DWORD PTR [ebp-4]
	mov ecx, 137
	imul ecx
	mov DWORD PTR [ebp-76], eax

# 49:+,220,$11,$12
instruction_label50:
	mov eax, DWORD PTR [ebp-76]
	mov edx, 220
	add eax, edx
	mov DWORD PTR [ebp-80], eax

# 50:+,i,$12,$13
instruction_label51:
	mov eax, DWORD PTR [ebp-80]
	mov edx, DWORD PTR [ebp-8]
	add eax, edx
	mov DWORD PTR [ebp-84], eax

# 51:%,101,$13,$14
instruction_label52:
	mov eax, DWORD PTR [ebp-84]
	cdq
	mov ecx, 101
	idiv ecx
	mov DWORD PTR [ebp-88], edx

# 52::=,$14,-,seed
instruction_label53:
	mov ecx, DWORD PTR [ebp-88]
	mov DWORD PTR [ebp-4], ecx

# 53:array,x,i,$15
instruction_label54:
	mov eax, DWORD PTR [ebp-8]
	mov ecx, 4
	imul ecx
	lea ecx, [ebp-72]
	add eax, ecx
	mov DWORD PTR [ebp-92], eax

# 54::=,seed,-,[$15]
instruction_label55:
	mov ecx, DWORD PTR [ebp-4]
	mov edi, DWORD PTR [ebp-92]
	mov DWORD PTR [edi], ecx

# 55:+,1,i,$16
instruction_label56:
	mov eax, DWORD PTR [ebp-8]
	mov edx, 1
	add eax, edx
	mov DWORD PTR [ebp-96], eax

# 56::=,$16,-,i
instruction_label57:
	mov ecx, DWORD PTR [ebp-96]
	mov DWORD PTR [ebp-8], ecx

# 57:jump,-,-,47
instruction_label58:
	jmp instruction_label47

# 58:array,x,5,$17
instruction_label59:
	mov eax, 5
	mov ecx, 4
	imul ecx
	lea ecx, [ebp-72]
	add eax, ecx
	mov DWORD PTR [ebp-100], eax

# 59:par,[$17],V,-
instruction_label60:
	mov edi, DWORD PTR [ebp-100]
	mov eax, DWORD PTR [edi]
	push eax

# 60:call,-,-,puti
instruction_label61:
	sub esp, 4
	mov esi, DWORD PTR [ebp+8]
	push esi
	call puti0
	add esp, 12

# 62:par,x,R,-
instruction_label63:
	lea esi, [ebp-72]
	push esi

# 61:par,16,V,-
instruction_label62:
	mov eax, 16
	push eax

# 63:call,-,-,putArray
instruction_label64:
	sub esp, 4
	push ebp
	call putArray1
	add esp, 16

# 65:par,x,R,-
instruction_label66:
	lea esi, [ebp-72]
	push esi

# 64:par,16,V,-
instruction_label65:
	mov eax, 16
	push eax

# 66:call,-,-,bsort
instruction_label67:
	sub esp, 4
	push ebp
	call bsort1
	add esp, 16

# 68:par,x,R,-
instruction_label69:
	lea esi, [ebp-72]
	push esi

# 67:par,16,V,-
instruction_label68:
	mov eax, 16
	push eax

# 69:call,-,-,putArray
instruction_label70:
	sub esp, 4
	push ebp
	call putArray1
	add esp, 16

# 70:endu,main,-,-
instruction_label71:
	pop esi
	pop ebx
	pop edi
	add esp, 100
 mov esp, ebp
	pop ebp
	ret


.data
	int_fmt: .asciz  "%d"
	char_fmt: .asciz  "%c"
	string_fmt: .asciz  "%c\n"
	printf_int: .asciz  "%d\n"
	printf_char: .asciz  "%c\n"
	printf_string: .asciz  "%s\n"
	prompt_int: .asciz  "Enter a number\n"
	prompt_char: .asciz  "Enter a character\n"
	prompt_string: .asciz  "Enter a string"
	prompt_string2: .asciz  "with length %d characters\n"
