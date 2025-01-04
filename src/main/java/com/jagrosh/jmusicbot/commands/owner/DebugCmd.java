/*
 * Copyright 2017 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.OwnerCommand;
import com.jagrosh.jmusicbot.utils.OtherUtil;
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;

/**
 *
 * @author John Grosh (john.a.grosh@gmail.com)
 */
public class DebugCmd extends OwnerCommand 
{
    private final static String[] PROPERTIES = {"java.version", "java.vm.name", "java.vm.specification.version", 
        "java.runtime.name", "java.runtime.version", "java.specification.version",  "os.arch", "os.name"};
    
    private final Bot bot;
    
    public DebugCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "debug";
        this.help = "показує інформацію для відлагодження";
        this.aliases = bot.getConfig().getAliases(this.name);
        this.guildOnly = false;
    }

    @Override
    protected void execute(CommandEvent event)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("```\nСистемні властивості:");
        for(String key: PROPERTIES)
            sb.append("\n  ").append(key).append(" = ").append(System.getProperty(key));
        sb.append("\n\nІнформація про JMusicBot:")
                .append("\n  Версія = ").append(OtherUtil.getCurrentVersion())
                .append("\n  Власник = ").append(bot.getConfig().getOwnerId())
                .append("\n  Префікс = ").append(bot.getConfig().getPrefix())
                .append("\n  Альтернативний префікс = ").append(bot.getConfig().getAltPrefix())
                .append("\n  Макс. кількість секунд = ").append(bot.getConfig().getMaxSeconds())
                .append("\n  Зображення для NP = ").append(bot.getConfig().useNPImages())
                .append("\n  Пісня в статусі = ").append(bot.getConfig().getSongInStatus())
                .append("\n  Залишатися в каналі = ").append(bot.getConfig().getStay())
                .append("\n  Використовувати Eval = ").append(bot.getConfig().useEval())
                .append("\n  Оповіщення про оновлення = ").append(bot.getConfig().useUpdateAlerts());
        sb.append("\n\nІнформація про залежності:")
                .append("\n  Версія JDA = ").append(JDAInfo.VERSION)
                .append("\n  Версія JDA-Utilities = ").append(JDAUtilitiesInfo.VERSION)
                .append("\n  Версія Lavaplayer = ").append(PlayerLibrary.VERSION);
        long total = Runtime.getRuntime().totalMemory() / 1024 / 1024;
        long used = total - (Runtime.getRuntime().freeMemory() / 1024 / 1024);
        sb.append("\n\nІнформація про виконання:")
                .append("\n  Загальна пам'ять = ").append(total)
                .append("\n  Використана пам'ять = ").append(used);
        sb.append("\n\nІнформація про Discord:")
                .append("\n  ID = ").append(event.getJDA().getSelfUser().getId())
                .append("\n  Сервери = ").append(event.getJDA().getGuildCache().size())
                .append("\n  Користувачі = ").append(event.getJDA().getUserCache().size());
        sb.append("\n```");
        
        if(event.isFromType(ChannelType.PRIVATE) 
                || event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_ATTACH_FILES))
            event.getChannel().sendFile(sb.toString().getBytes(), "debug_information.txt").queue();
        else
            event.reply("Інформація для налагодження: " + sb.toString());
    }
}
