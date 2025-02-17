import discord
from discord.ext import commands
import re
import subprocess
import os
import sys
import asyncio

# Config du bot avec les bons intents
intents = discord.Intents.default()
intents.messages = True
intents.message_content = True  # Permet de récup le contenu des msgs
bot = commands.Bot(command_prefix='!', intents=intents)

# Ajout du chemin à PATH (évite erreurs sur certains systèmes)
os.environ["PATH"] += ":/usr/bin"

# Liste des mots toxiques pr analyse
toxic_words = [
    'idiot', 'stupide', 'nul', 'con', 'abruti', 'débile', 'fou', 'taré', 
    'crétin', 'chiant', 'merde', 'pute', 'salope', 'fdp', 'enculé', 'batard',
    'connard', 'dégage', 'ferme', 'gueule', 'crève', 'clochard', 'déchet',
    'minable', 'bouffon', 'abrutis', 'dégueulasse', 'détestable', 'imbécile',
    'rageux', 'ridicule', 'pauvre', 'misérable', 'toxique', 'honteux',
    'pitoyable', 'raté', 'fragile', 'branleur', 'boloss', 'teubé', 'tocard'
]

# Nettoie les mentions et éléments indésirables ds un msg
def clean_message(content, message):
    for user in message.mentions:
        content = content.replace(f'<@{user.id}>', f'@{user.display_name}')
    content = discord.utils.escape_markdown(content)
    content = discord.utils.escape_mentions(content)
    return content

# Compile et exécute un fichier Java
async def run_java():
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
            return f"Résultat de l'exécution :\n{output}"
        else:
            return f"Erreur lors de l'exécution :\n{error}"
        
    except Exception as e:
        return f"Erreur inattendue : {str(e)}"

# Commande Discord pr exécuter le Java
@bot.command()
async def java(ctx):
    """ Compile et exécute le programme Java via Discord """
    result = await run_java()

    # Gestion des msgs trop longs pr Discord (limite 2000 caractères)
    if len(result) > 2000:
        for i in range(0, len(result), 1900):
            chunk = result[i:i+1900]
            await ctx.send(f"```\n{chunk}\n```")
    else:
        await ctx.send(f"```\n{result}\n```")

# Événement déclenché quand le bot est prêt
@bot.event
async def on_ready():
    print(f'{bot.user} est connecté à Discord!')

    # Vérif si une cmd a été passée via terminal après co
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
                        print(f"Erreur : Impossible de récup le salon avec ID {channel_id}.")
                else:
                    print("Erreur : Aucun ID de channel spécifié.")
            except Exception as e:
                print(f"Erreur : {e}")

        elif command == "java":
            async def run():
                result = await run_java()
                print(result)
                os._exit(0)  # Quitte proprement après exécution
            asyncio.create_task(run())

# Fonction pr récupérer et stocker des msgs d'un channel
async def selection_from_webhook(nb_messages, channel):
    file_path = "/home/rt/Téléchargements/SAE-JAVADISCORD/selected_messages.txt"

    try:
        with open(file_path, 'w', encoding='utf-8') as file:
            async for message in channel.history(limit=nb_messages):
                cleaned_content = clean_message(message.content, message)
                timestamp = message.created_at.strftime('%Y-%m-%d %H:%M:%S')

                #  Calcul du score de toxicité
                words = re.findall(r'\b\w+\b', cleaned_content.lower())
                toxicity_score = sum(words.count(word) for word in toxic_words)

                reactions = [f'{reaction.emoji}: {reaction.count}' for reaction in message.reactions]
                attachments = [attachment.url for attachment in message.attachments]
                mentions = [f'@{user.display_name}' for user in message.mentions]
                message_length = len(message.content)
                edited_at = message.edited_at.strftime('%Y-%m-%d %H:%M:%S') if message.edited_at else None

                # Écriture ds le fichier
                file.write(f'[{timestamp}] {message.author}: {cleaned_content}\n')
                file.write(f'Toxicity Score: {toxicity_score}\n')
                if reactions:
                    file.write(f'Reactions: {", ".join(reactions)}\n')
                if attachments:
                    file.write(f'Attachments: {", ".join(attachments)}\n')
                if mentions:
                    file.write(f'Mentions: {", ".join(mentions)}\n')
                file.write(f'Message length: {message_length}\n')
                if edited_at:
                    file.write(f'Edited at: {edited_at}\n')
                file.write('\n')

        os.chmod(file_path, 0o644)
        print(f"{nb_messages} derniers msgs enregistrés ds {file_path}.")

    except Exception as e:
        print(f"Erreur écriture fichier : {str(e)}")

# Commande pr récupérer des msgs d'un channel
@bot.command()
async def selection(ctx, nb_messages: int, channel: discord.TextChannel = None):
    """ Récup les derniers `nb_messages` d'un channel et les stocke """
    if channel is None:
        channel = ctx.channel  # Prend le channel actuel si aucun précisé
    await selection_from_webhook(nb_messages, channel)

# Lancer le bot
bot.run('mettre le token du bot')
