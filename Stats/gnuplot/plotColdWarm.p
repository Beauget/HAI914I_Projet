# Make the x axis labels easier to read.
set encoding utf8
set style data histogram
set style fill solid border
# set the labels
set terminal wxt size 1300,600
set title 'Comparaison temps éxecution entre Jena et notre projet 500K'
set ylabel 'Temps (en ms)'
set style histogram clustered
plot for [COL=2:3] 'comparaisonJena.tsv' using COL:xticlabels(1) title columnheader
