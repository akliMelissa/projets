#!/bin/bash

# Initialisation de variables
FILE="$1"
i=0

# Vérifier si le fichier existe
if [[ ! -f "$FILE" ]]; then
    echo "Le fichier $FILE n'existe pas."
    exit 1
fi

# Lire le contenu des fichiers avant et après
avant=$(arm-none-eabi-readelf -s "$FILE")
./Program2 "$FILE" "$FILE.modified" >> /dev/null
apres=$(arm-none-eabi-readelf -s "$FILE.modified")
x=0
# Comparer les symboles ligne par ligne
while IFS=$'\n' read -r line; do
    if [[ $i -lt 2 ]]; then
        ((i++))
        continue
    fi

    # Extraire la ligne correspondante dans 'apres'
    avant_line=$(echo "$avant" | sed -n "${i}p")
    apres_line=$(echo "$apres" | sed -n "${i}p")
    #extraire NDX avant et apres
    avant_NDX=$(echo "$avant_line" | awk '{print $7}')
    apres_NDX=$(echo "$apres_line" | awk '{print $7}')
    #sil sont differents

    if [[ "$avant_NDX" != "$apres_NDX" ]]; then
        #extraire le nom de la section avant
        avant_section=$(echo "$avant_line" | awk '{print $8}')
        #si le nom de section ca commence avec un .
        Table_de_section_apres=$(arm-none-eabi-readelf -S "$FILE.modified")
        if [[ "$avant_section" == .* ]]; then
            
            #extraire la ligne avec le nom avant section 
            ligne_apres=$(echo "$Table_de_section_apres" | grep -n "$avant_section")
            #extraire dans quelle num de ligne il se trouve ligne apres das table de section
            #extraire le num de la section
            
            num_section=$(echo "$ligne_apres" | awk '{print $1}')
            #enlever le dernier caractere
            num_section=${num_section%?}

            #-5 pour avoir le num de la section
            num_section=$((num_section - 5))

            
            #comparer ndx apres et num de section
            if [[ "$apres_NDX" != "$num_section" ]]; then
                echo -e "\e[31mLes symboles ne sont pas correctement corrigés pour le fichier $1\e[0m"
                echo "Ligne --$ligne_apres--"
                echo "NDx apres --$apres_NDX--"
                echo "Num section --$num_section--"
                exit 1
            fi

            

        fi
        
    fi
    #test 2
            if [[ $i -lt 5 ]]; then
                ((i++))
                continue
            fi

            #extraire valeure pour avant et apres 
            avant_valeur=$(echo "$avant_line" | awk '{print $2}')
            apres_valeur=$(echo "$apres_line" | awk '{print $2}')
            #si valeure avant et apres ne sont pas numerique
            

            #convertir en decimal valeure avant et apres 
            avant_valeur=$(echo $((16#$avant_valeur)))
            apres_valeur=$(echo $((16#$apres_valeur)))

            Table_de_section_apres=$(arm-none-eabi-readelf -S "$FILE.modified")
            #extraire le adresse avec le num apres_ndx
            
            if [[ $apres_NDX -lt 10 ]]; then 
                adresse=$(echo "$Table_de_section_apres" | awk -v num=$apres_NDX 'NR==num+5 {print $5}')
            else
                adresse=$(echo "$Table_de_section_apres" | awk -v num=$apres_NDX 'NR==num+5 {print $4}')
            fi
            #convertir en decimal
            adresse=$(echo $((16#$adresse)))
            #adresse + valeur avant doit etre egale a valeur apres
            if [[ $((adresse + avant_valeur)) -ne $apres_valeur ]]; then
                echo -e "\e[31mLes symboles ne sont pas correctement corrigés pour le fichier $1\e[0m"
                exit 1
            fi
    ((i++))
    
done < <(echo "$avant")


# Affichage en vert si tout est correct
echo -e "\e[32mLes symboles sont correctement corrigés pour le fichier $1\e[0m"

exit 0