import discord
from discord.ext import commands
import re
import subprocess
import os
import sys
import asyncio


intents = discord.Intents.default()
intents.messages = True
intents.message_content = True  
bot = commands.Bot(command_prefix='!', intents=intents)


os.environ["PATH"] += ":/usr/bin"


toxic_words = ['badword1', 'badword2']


def clean_message(content, message):
    for user in message.mentions:
        content = content.replace(f'<@{user.id}>', f'@{user.display_name}')
    content = discord.utils.escape_markdown(content)
    content = discord.utils.escape_mentions(content)
    return content


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


@bot.command()
async def java(ctx):
    """ Compile et exécute le programme Java via Discord """
    result = await run_java()
    await ctx.send(f"```\n{result}\n```")


@bot.event
async def on_ready():
    print(f'{bot.user} est connecté à Discord!')

   
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
                        print(f"Erreur : Impossible de récupérer le salon avec l'ID {channel_id}.")
                else:
                    print("Erreur : Aucun ID de channel spécifié.")
            except Exception as e:
                print(f"Erreur : {e}")

        elif command == "java":
            async def run():
                result = await run_java()
                print(result)
                os._exit(0) n
            asyncio.create_task(run())


async def selection_from_webhook(nb_messages, channel):
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
                if message.reference:
                    try:
                        referenced_message = await channel.fetch_message(message.reference.message_id)
                    except (discord.NotFound, discord.Forbidden):
                        referenced_message = None

              
                file.write(f'[{timestamp}] {message.author}: {cleaned_content}\n')
                if toxicity:
                    file.write(f'Toxicity: {toxicity}\n')
                if reactions:
                    file.write(f'Reactions: {", ".join(reactions)}\n')
                if attachments:
                    file.write(f'Attachments: {", ".join(attachments)}\n')
                if mentions:
                    file.write(f'Mentions: {", ".join(mentions)}\n')
                if roles:
                    file.write(f'Roles: {", ".join(roles)}\n')
                if urls:
                    file.write(f'Links: {", ".join(urls)}\n')
                file.write(f'Message length: {message_length}\n')
                if edited_at:
                    file.write(f'Edited at: {edited_at}\n')
                if referenced_message:
                    file.write(f'Replies to: {referenced_message.author}: {referenced_message.content}\n')
                file.write('\n')

        os.chmod(file_path, 0o644)
        print(f"{nb_messages} derniers messages ont été enregistrés dans {file_path}.")

    except Exception as e:
        print(f"Erreur lors de l'écriture du fichier : {str(e)}")


@bot.command()
async def selection(ctx, nb_messages: int, channel: discord.TextChannel = None):
    """ Récupère les derniers `nb_messages` d'un channel et les stocke. """
    if channel is None:
        channel = ctx.channel 
    await selection_from_webhook(nb_messages, channel)


bot.run('')
