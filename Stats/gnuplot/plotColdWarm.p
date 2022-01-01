----WARM-----
----iND

# Make the x axis labels easier to read.
set encoding utf8
set style data histogram
set style fill solid border
# set the labels
set datafile separator ','
set terminal wxt size 700,600
#set title 'Comparaison des performances suivant la quantité de RAM alloué'
set ylabel 'Temps (en ms)'
#set xlabel 'Création des indexs'
set style histogram clustered
plot for [COL=2:4] 'warm500KIndex.tsv' using COL:xticlabels(1) title columnheader
set yr [0:]
replot

--REQ

# Make the x axis labels easier to read.
set encoding iso_8859_15
set style data histogram
set style fill solid border
# set the labels
set datafile separator ','
set terminal wxt size 700,600
#set title 'Comparaison des performances suivant la quantité de RAM alloué'
set ylabel 'Temps (en ms)'
#set xlabel 'Exécution des requêtes'
set style histogram clustered
plot for [COL=2:4] 'warm500KRequete.tsv' using COL:xticlabels(1) title columnheader
set yr [0:]
replot


----COLD----
--REQ
# Make the x axis labels easier to read.
set encoding utf8
set style data histogram
set style fill solid border
# set the labels
set datafile separator ','
set terminal wxt size 1300,600
#set title 'Comparaison des performances suivant la quantité de RAM alloué'
set ylabel 'Temps (en ms)'
set style histogram clustered
plot for [COL=2:4] 'cold500KReq.tsv' using COL:xticlabels(1) title columnheader
set yr [0:]
replot


--DIC
# Make the x axis labels easier to read.
set encoding utf8
set style data histogram
set style fill solid border
# set the labels
set datafile separator ','
set terminal wxt size 1300,600
#set title 'Comparaison des performances suivant la quantité de RAM alloué'
set ylabel 'Temps (en ms)'
set style histogram clustered
plot for [COL=2:4] 'cold500KDico.tsv' using COL:xticlabels(1) title columnheader
set yr [0:600]
replot
