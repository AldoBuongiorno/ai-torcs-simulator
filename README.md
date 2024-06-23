
# Gran Premio MIVIA 2024

Il progetto consiste nella realizzazione di un algoritmo di guida autonoma, il quale deve permettere, in base ad un data-set costruito su azioni di un pilota, di far compiere giri di pista ad un veicolo, autonomamente.
Il veicolo di ciascun gruppo dovrà quindi completare il giro nel minor tempo possibile, quest’ultimo selezionato tra i tempi conseguiti in un numero determinato di giri, senza presenza di altri giocatori.





## How to compile and execute the code 

If you want to run the program we developed, you have at first to select the dataset, after you have to choose the configuration you want the code to run by, then you can execute it.

Once you have completed the previous displayed tasks, you can firstly compile the code, opening a terminal window into the `'Torc/src'` folder and running the following commands:
```bash
javac -d ../classes scr/*.java
```
After you have compiled the code, you can run it, opening a terminal window into the `'Torc/classes'` folder, running the following commands:
```bash
java scr.Client scr.SimpleDriver host:localhost port:3001 verbose:on
```

### How to select the data-set

To select the data-set you have to change the variable `'dataRetrievalFileName'` value. 
```java
if (classify) {
	dataRetrievalFileName = "";//Change this in ordet to select the dataset path.
    knn = new NearestNeighbor(dataRetrievalFileName);
}
```
### How to select the configuration

In order to select the configuration which you want the code will run by, you have to set the configuration variables you can find into the class `'SimpleDriver'` constructor, on `'true'` or `'false'`.
```java
public SimpleDriver(){
	auto = false;//true to let the car drive in initial autonomous mode
	train = false;//true to drive the car in manual mode
	classify = false;//true to let the car drive in autonomous mode
    ...
```
