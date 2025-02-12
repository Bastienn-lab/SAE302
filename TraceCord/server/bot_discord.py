import discord
from discord.ext import commands
import re
import subprocess
import os
import sys
import asyncio

#  CONFIGURATION DU BOT  
# On active les "intents" pr permettre au bot de voir les msgs + interagir avec le contenu  
intents = discord.Intents.default()
intents.messages = True
intents.message_content = True  

# Création du bot avec "!" comme préfixe de cmd  
bot = commands.Bot(command_prefix='!', intents=intents)

#  CONFIGURATION ENVIRONNEMENT  
# Ajout du chemin vers les exécutables pr exécuter Java depuis le script  
os.environ["PATH"] += ":/usr/bin"

#  LISTE DES MOTS "TOXIQUES"  
# Ces mots seront utilisés pr détecter un msg potentiellement problématique  
toxic_words = ['badword1', 'badword2']

def clean_message(content, message):
    """
    Nettoie le msg :
    - Remplace les mentions directes (<@user_id>) par @pseudo
    - Échappe les caractères Markdown pr éviter la mise en forme auto
    - Échappe aussi les mentions (@everyone, @here)
    """
    for user in message.mentions:
        content = content.replace(f'<@{user.id}>', f'@{user.display_name}')
    content = discord.utils.escape_markdown(content)
    content = discord.utils.escape_mentions(content)
    return content

async def run_java():
    """
     Compile et exécute le programme Java via une commande shell
    - Utilisé pr traiter et analyser les msgs Discord
    - Retourne le résultat ou une erreur si ça plante
    """
    try:
        command = """
        /usr/bin/javac /home/rt/Téléchargements/SAE-JAVADISCORD/DiscordDataProcessor.java &&
        /usr/bin/java -cp /usr/share/java/mysql-connector-java-9.2.0.jar:/home/rt/Téléchargements/SAE-JAVADISCORD DiscordDataProcessor
        """
        execute_process = subprocess.run(
            ["/bin/bash", "-c", command],
            capture_output=True,
            text=True
        )

        output = execute_process.stdout.strip()
        error = execute_process.stderr.strip()

        if execute_process.returncode == 0:
            return f"✅ Résultat de l'exécution :\n{output}"
        else:
            return f"❌ Erreur lors de l'exécution :\n{error}"
        
    except Exception as e:
        return f"❌ Erreur inattendue : {str(e)}"

@bot.command()
async def java(ctx):
    """
     CMD "!java" → Compile et exécute le programme Java directement via Discord  
    """
    result = await run_java()
    await ctx.send(f"```\n{result}\n```")

@bot.event
async def on_ready():
    """
     Event appelé qd le bot est prêt  
    - Affiche un msg dans la console  
    - Vérifie si le bot a été lancé avec des arguments pr exécuter des cmd spécifiques
    """
    print(f'✅ {bot.user} est connecté à Discord!')

    # ⚙ Gestion des args passés au lancement du bot  
    if len(sys.argv) >= 2:
        command = sys.argv[1]

        if command == "selection":
            try:
                nb_messages = int(sys.argv[2])
                channel_id = int(sys.argv[3]) if len(sys.argv) > 3 else None
                if channel_id:
                    channel = bot.get_channel(channel_id)
                    if channel:
                        asyncio.create_task(selection_from_webhook(nb_messages, channel))
                    else:
                        print(f"❌ Erreur : Impossible de récupérer le salon avec l'ID {channel_id}.")
                else:
                    print("❌ Erreur : Aucun ID de channel spécifié.")
            except Exception as e:
                print(f"❌ Erreur : {e}")

        elif command == "java":
            async def run():
                result = await run_java()
                print(result)
                os._exit(0)  # Quitter proprement après exécution
            asyncio.create_task(run())

async def selection_from_webhook(nb_messages, channel):
    """
     Récupère les `nb_messages` derniers msgs d'un salon  
    - Nettoie les données
    - Stocke les infos utiles dans un fichier texte
    """
    file_path = '/home/rt/Téléchargements/SAE-JAVADISCORD/selected_messages.txt'

    try:
        with open(file_path, 'w', encoding='utf-8') as file:
            async for message in channel.history(limit=nb_messages):
                cleaned_content = clean_message(message.content, message)
                timestamp = message.created_at.strftime('%Y-%m-%d %H:%M:%S')
                toxicity = any(word in message.content.lower() for word in toxic_words)
                reactions = [f'{reaction.emoji}: {reaction.count}' for reaction in message.reactions]
                attachments = [attachment.url for attachment in message.attachments]
                mentions = [f'@{user.display_name}' for user in message.mentions]
                roles = [role.name for role in getattr(message.author, 'roles', []) if hasattr(message.author, 'roles')]
                urls = re.findall(r'(https?://\S+)', message.content)
                message_length = len(message.content)
                edited_at = message.edited_at.strftime('%Y-%m-%d %H:%M:%S') if message.edited_at else None
                referenced_message = None
                
                #  Vérifie si le msg est une réponse à un autre msg  
                if message.reference:
                    try:
                        referenced_message = await channel.fetch_message(message.reference.message_id)
                    except (discord.NotFound, discord.Forbidden):
                        referenced_message = None

                #  Enregistrement des données dans le fichier  
                file.write(f'[{timestamp}] {message.author}: {cleaned_content}\n')
                if toxicity:
                    file.write(f'Toxicité détectée: {toxicity}\n')
                if reactions:
                    file.write(f'Réactions: {", ".join(reactions)}\n')
                if attachments:
                    file.write(f'Fichiers joints: {", ".join(attachments)}\n')
                if mentions:
                    file.write(f'Mentions: {", ".join(mentions)}\n')
                if roles:
                    file.write(f'Rôles: {", ".join(roles)}\n')
                if urls:
                    file.write(f'Liens: {", ".join(urls)}\n')
                file.write(f'Taille du message: {message_length} caractères\n')
                if edited_at:
                    file.write(f'Modifié le: {edited_at}\n')
                if referenced_message:
                    file.write(f'Répond à: {referenced_message.author}: {referenced_message.content}\n')
                file.write('\n')

        #  Donne les permissions de lecture/écriture au fichier  
        os.chmod(file_path, 0o644)
        print(f"✅ {nb_messages} derniers msgs ont été enregistrés dans {file_path}.")

    except Exception as e:
        print(f"❌ Erreur lors de l'écriture du fichier : {str(e)}")

@bot.command()
async def selection(ctx, nb_messages: int, channel: discord.TextChannel = None):
    """
     CMD "!selection nb_messages [channel]"  
    - Récupère les derniers msgs d'un salon et les stocke
    - Si aucun salon n'est précisé, récupère les msgs du salon actuel
    """
    if channel is None:
        channel = ctx.channel 
    await selection_from_webhook(nb_messages, channel)

#  Lancement du bot (remplacer '' par le token Discord)
bot.run('')
