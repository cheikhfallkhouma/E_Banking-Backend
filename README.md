# E-Banking
Le projet consiste à développer une application monolithique en Java avec Spring Boot qui gère des comptes bancaires avec des clients.
On peut faire les opérations suivantes:
-consulter le solde du compte
-retirer, virer et transferer
-consulter l'historique des opérations d'un compte
Une authentifcation de sécurité avec des JWT a été mise en place. Ceci permettra de protèger certaines actions notamment opérations de virement, de retrait et de transfert. Seuls les profiles de type ADMIN peuvent effectuer ces opérations.
