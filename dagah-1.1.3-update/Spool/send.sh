
while :
do
n=`expr $RANDOM / 900`
echo n $n
date
python qp.py python dagah2.py RUN list camp $n
sleep $n
done
