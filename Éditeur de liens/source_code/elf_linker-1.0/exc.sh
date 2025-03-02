#!/bin/bash

# Check if a file is provided as an argument
if [ -z "$1" ]; then
    echo "Usage: $0 <elf_file>"
    exit 1
fi

# Check if the file is readable
if [ ! -r "$1" ]; then
    echo "Error: Cannot read file $1."
    exit 2
fi

# Get program headers using readelf -l
program_headers=$(readelf -l "$1" 2>/dev/null)

#echo name of the file
echo "File: $1"

# Check if readelf executed successfully
if [ $? -ne 0 ]; then
    echo "Error: Problem executing readelf on $1."
    exit 3
fi

# Arrays to hold segment start addresses, sizes, and other details
declare -a starts
declare -a sizes
declare -a offsets
declare -a virt_addrs
declare -a file_sizes
index=0

# Extract VirtAddr, Offset, and FileSiz of LOAD segments
echo "LOAD segment addresses, offsets, and file sizes:"
echo "$program_headers" | grep -i "LOAD" | while read -r line; do
    virtaddr=$(echo $line | awk '{print $3}')
    offset=$(echo $line | awk '{print $2}')
    filesize=$(echo $line | awk '{print $5}')
    
    # Print segment details
    echo "Address: $virtaddr, Offset: $offset, FileSiz: $filesize"
    
    # Remove '0x' prefix for decimal conversion
    virtaddr=${virtaddr#0x}
    filesize=${filesize#0x}
    
    # Store in arrays for overlap check
    virt_addrs[index]=$virtaddr
    offsets[index]=$offset
    file_sizes[index]=$filesize
    # Convert to decimal for calculations
    starts[index]=$((16#$virtaddr))
    sizes[index]=$((16#$filesize))
    index=$((index + 1))
done

# Check if there are less than two segments


# Check for overlaps between each pair of segments
overlap_found=false
for i in "${!starts[@]}"; do
    for j in "${!starts[@]}"; do
        if [ $i -lt $j ]; then
            # Check if segments overlap
            if [ ${starts[i]} -lt $((${starts[j]} + ${sizes[j]})) ] && [ ${starts[j]} -lt $((${starts[i]} + ${sizes[i]})) ]; then
                echo "Overlap detected between segment $i and segment $j:"
                echo "  Segment $i: Address: 0x${virt_addrs[i]}, Offset: ${offsets[i]}, FileSiz: 0x${file_sizes[i]}"
                echo "  Segment $j: Address: 0x${virt_addrs[j]}, Offset: ${offsets[j]}, FileSiz: 0x${file_sizes[j]}"
                overlap_found=true
            fi
        fi
    done
done
if [ "$overlap_found" = false ]; then
    #en vert
    echo -e "\e[32mTest passed\e[0m"
    
else
    #en rouge
    echo -e "\e[31mTest failed\e[0m"
fi
echo "************************************"