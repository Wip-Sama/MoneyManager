package org.wip.moneymanager.model;

import java.util.ArrayList;
import java.util.List;

public class Data {
    // Non serve ma volevo metterala lo stesso
    public static final MMDatabase mmDatabase = MMDatabase.getInstance();

    //public static String user = null;
    // Solo per i test
    public static String user = "user";
    // La vera domanda è come gestiamo il cambio di utente?
    // Non è che siamo in un casino totale?
    // Ho il dubbio che dovremmo in qualche modo chiudere tutti i db e poi aprirli di nuovo
    // basterà fare qualcosa con l'istanza di UserDatabase?

    public static UserDatabase userDatabase;
    // Teoricamente ma ho una strana sensazione riguardo alla possibilità che più db siano aperti insieme
    // Questa cosa non è supportata ma la sensazione non se ne va
    public static final List<UserDatabase> userDatabasesList = new ArrayList<>();
}
