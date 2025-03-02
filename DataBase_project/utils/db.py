import sqlite3
from datetime import datetime
from sqlite3 import IntegrityError
import pandas

# Pointeur sur la base de données
data = sqlite3.connect("data/climat_france.db")
data.execute("PRAGMA foreign_keys = 1")

# Fonction permettant d'exécuter toutes les requêtes sql d'un fichier
# Elles doivent être séparées par un point-virgule
def updateDBfile(data:sqlite3.Connection, file):

    # Lecture du fichier et placement des requêtes dans un tableau
    createFile = open(file, 'r')
    createSql = createFile.read()
    createFile.close()
    sqlQueries = createSql.split(";")

    # Exécution de toutes les requêtes du tableau
    cursor = data.cursor()
    for query in sqlQueries:
        cursor.execute(query)

# Action en cas de clic sur le bouton de création de base de données
def createDB():
    try:
        # On exécute les requêtes du fichier de création
        updateDBfile(data, "data/createDB.sql")
    except Exception as e:
        print ("L'erreur suivante s'est produite lors de la création de la base : " + repr(e) + ".")
    else:
        data.commit()
        print("Base de données créée avec succès.")

# En cas de clic sur le bouton d'insertion de données
#TODO Q4 Modifier la fonction insertDB pour insérer les données dans les nouvelles tables
def insertDB():
    try:
        # Définir le curseur à partir de la connexion SQLite
        cursor = data.cursor()
        # Insertion des régions
        read_csv_file(
            "data/csv/Communes.csv", ';',
            "insert into Regions values (?,?)",
            ['Code Région', 'Région']
        )
        read_csv_file(
            "data/csv/AnciennesNouvellesRegions.csv", ';',
            "insert into Regions values (?,?)",
            ['Nouveau Code', 'Nom Officiel Région Majuscule']
        )

        # Insertion des départements
        read_csv_file(
            "data/csv/Communes.csv", ';',
            "insert into Departements (code_departement, nom_departement,code_region) values (?, ?, ?)",
            ['Code Département', 'Département', 'Code Région']
        )
        read_csv_file(
            "data/csv/ZonesClimatiques.csv", ';',
            "update Departements set zone_climatique = ? where code_departement = ?",
            ['zone_climatique', 'code_departement']
        )

        # Mise à jour des régions et suppression des anciennes régions
        read_csv_file(
            "data/csv/AnciennesNouvellesRegions.csv", ';',
            "update Departements set code_region = ? where code_region = ?",
            ['Nouveau Code', 'Anciens Code']
        )
        read_csv_file(
            "data/csv/AnciennesNouvellesRegions.csv", ';',
            "delete from Regions where code_region = ? and ? <> ?",
            ['Anciens Code', 'Anciens Code', 'Nouveau Code']
        )
        print("Les erreurs UNIQUE constraint sont normales car on insère une seule fois les Regions et les Départements")
        print("Insertion de mesures en cours...cela peut prendre un peu de temps")
        # Insertion des mesures
        read_csv_file(
            "data/csv/Mesures.csv", ';',
            "insert into Mesures values (?, ?, ?, ?, ?)",
            ['code_insee_departement', 'date_obs', 'tmin', 'tmax', 'tmoy']
        )

        # Insertion des communes
        read_csv_file(
            "data/csv/Communes.csv", ';',
            """
            insert into Communes 
            (code_departement, code_commune, nom_commune, status, altitude_moyenne, population, superficie, 
             code_canton, code_arrondissement) 
            values (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
            [
                'Code Département', 'Code Commune', 'Commune', 'Statut', 'Altitude Moyenne', 'Population',
                'Superficie', 'Code Canton', 'Code Arrondissement'
            ]
        )

        # Gestion des travaux
        for csv_file, travaux_query, detail_query, travaux_columns, detail_columns in [
            (
                "data/csv/Isolation.csv",
                """
                INSERT INTO Travaux
                (cout_total_ht_travaux, cout_induit_ht_travaux, date_travaux, type_logement_travaux,
                annee_construction_logement_travaux, code_region, code_departement)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                """
                INSERT INTO Isolations
                (id_travaux, poste_isolation, isolant_isolation, epaisseur_isolation, surface_isolation)
                VALUES (?, ?, ?, ?, ?)
                """,
                ['cout_total_ht', 'cout_induit_ht', 'annee_travaux', 'type_logement', 'annee_construction', 'code_region', 'code_departement'],
                ['poste_isolation', 'isolant', 'epaisseur', 'surface']
            ),
            (
                "data/csv/Chauffage.csv",
                """
                INSERT INTO Travaux
                (cout_total_ht_travaux, cout_induit_ht_travaux, date_travaux, type_logement_travaux,
                annee_construction_logement_travaux, code_region, code_departement)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                """
                INSERT INTO Chauffages
                (id_travaux, energie_avt_travaux_chauffage, energie_installee_chauffage, generateur_chauffage, type_chaudiere_chauffage)
                VALUES (?, ?, ?, ?, ?)
                """,
                ['cout_total_ht', 'cout_induit_ht', 'annee_travaux', 'type_logement', 'annee_construction', 'code_region', 'code_departement'],
                ['energie_chauffage_avt_travaux', 'energie_chauffage_installee', 'generateur', 'type_chaudiere']
            ),
            (
                "data/csv/Photovoltaique.csv",
                """
                INSERT INTO Travaux
                (cout_total_ht_travaux, cout_induit_ht_travaux, date_travaux, type_logement_travaux,
                annee_construction_logement_travaux, code_region)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                """
                INSERT INTO Photovoltaiques
                (id_travaux, puissance_installee_photovoltaique, type_panneaux_photovoltaique)
                VALUES (?, ?, ?)
                """,
                ['cout_total_ht', 'cout_induit_ht', 'annee_travaux', 'type_logement', 'annee_construction', 'code_region'],
                ['puissance_installee', 'type_panneaux']
            )
        ]:
            df = pandas.read_csv(csv_file, sep=';').where(pandas.notnull, None)
            for ix, row in df.iterrows():
                cursor.execute(travaux_query, tuple(row[travaux_columns]))
                id_travaux = cursor.lastrowid
                cursor.execute(detail_query, (id_travaux, *[row[col] for col in detail_columns]))

        data.commit()
        print("Un jeu de test a été inséré dans la base avec succès.")

    except Exception as e:
        print("L'erreur suivante s'est produite lors de l'insertion des données : " + repr(e))
        data.rollback()


# En cas de clic sur le bouton de suppression de la base
def deleteDB():
    try:
        updateDBfile(data, "data/deleteDB.sql")
    except Exception as e:
        print ("L'erreur suivante s'est produite lors de la destruction de la base : " + repr(e) + ".")
    else:
        data.commit()
        print("La base de données a été supprimée avec succès.")

def read_csv_file(csvFile, separator, query, columns):

    df = pandas.read_csv(csvFile, sep=separator)
    df = df.where(pandas.notnull(df), None)

    cursor = data.cursor()
    for ix, row in df.iterrows():
        try:
            tab = []
            for i in range(len(columns)):
                # pour échapper les noms avec des apostrophes, on remplace dans les chaines les ' par ''
                if isinstance(row[columns[i]], str):
                    row[columns[i]] = row[columns[i]].replace("'","''")
                tab.append(row[columns[i]])

            #print(query)
            cursor.execute(query, tuple(tab))
        except IntegrityError as err:
            print(err)

