# Guide utilisateur Apolline

------

### Sommaire

* [Introduction](#anchorIntro)
* [Datalogger](#anchorDatalogger)
  * [Préparation des fichiers de configuration](#anchorPrepa)
    * [Module](#anchorModulePrepa)
    * [Linux / Mac](#anchorLinPrepa)
  * [Transfert des fichiers sur le module](#anchorTrans)
    * [Windows](#anchorTransWin)
    * [Linux / Mac](#anchorTransLin)
  * [Lancement d'une configuration](#anchorLaunch)
  * [Arrêt d'une configuration](#anchorStopConfig)
  * [Arrêt du raspberry](#anchorStop)

------

### <span id="anchorIntro"></span>Introduction

### <span id="anchorDatalogger"></span>Datalogger

#### <span id="anchorPrepa"></span>Préparation des fichiers de configuration

##### <span id="anchorModulePrepa"></span>Module

> ##### Prérequis
>
> Sur Windows, il est nécessaire d'utiliser un logiciel comme [Putty](http://www.putty.org/) afin de communiquer avec un module

Pour communiquer avec un module, il faut tout d'abord se connecter au module avec la commande suivante :

```shell
$ ssh <nom d'utilisateur>@<ip du module>
<nom d'utilisateur>@<ip du module>'s password:
```

Une fois connecté au module, il faut connaître sur quelles ports USB sont branchés les capteurs. Pour chacun d'eux, il faut lancer le script *detectUSB.sh* avec la commande suivante :

```shell
$ sudo ./detectUSB.sh
Insérer le périphérique USB puis appuyer sur 'Entrée'
```

> ##### Avertissement
>
> Dans le cas d'un port **__/dev/ttyACM__**, il faut créer un alias avec la commande :
> ```shell
$ sudo ln -s /dev/ttyACM0 /dev/ttyUSB0
```

Pour terminer, il faut créer la configuration de chaque port USB sur le site d'[APOLLINE](http://apolline.apisense.io/) dans la page du module courant.

##### <span id="anchorLinPrepa"></span>Linux / Mac

Pour créer une configuration d'un capteurs branché sur Linux / Mac, il faut connaître sur quelle port USB est branchés le capteur. Pour cela, il faut lancer le script *detectUSB.sh* avec la commande suivante :

```shell
$ sudo ./detectUSB.sh
Insérer le périphérique USB puis appuyer sur 'Entrée'
```

> ##### Avertissement
>
> Dans le cas d'un port **__/dev/ttyACM__**, il faut créer un alias avec la commande :
> ```shell
$ sudo ln -s /dev/ttyACM0 /dev/ttyUSB0
```

Puis, il faut créer la configuration de chaque port USB sur le site d'[APOLLINE](http://apolline.apisense.io/) dans la page du module courant.

#### <span id="anchorTrans"></span>Transfert des fichiers sur le module

Pour le bon fonctionnement d'un module, les fichiers *detectUSB.sh*, *datalogger.jar*, *launcher.sh* et les fichiers de configuration en .properties doivent être présent sur le module.

##### <span id="anchorTransWin"></span>Windows

Pour transférer les fichiers sur un module à partir de Windows, il faut utiliser un logiciel comme [WinSCP](https://winscp.net/eng/docs/lang:fr) en utilisant le protocol SCP.

##### <span id="anchorTransLin"></span>Linux / Mac

Pour transférer les fichiers sur un module à partir de Linux ou de Mac, il faut, pour chaque fichier, lancer la commande suivante :

```shell
$ scp <nom du fichier source> <nom d'utilisateur>@<ip du module>:<nom du fichier de destination>
```

#### <span id="anchorLaunch"></span>Lancement d'une configuration

Pour démarrer une configuration, il faut lancer le datalogger avec la commande suivante :

```shell
$ java -jar datalogger.jar -p <le fichier de configuration en .properties>
```

Pour démarer l'ensemble des configurations sur un module, sur Linux ou sur Mac, il faut démarrer le script *launcher.sh* tel que :

```shell
$ ./launcher.sh
```

#### <span id="anchorStop"></span>Arrêt du raspberry
