package jiekie.model;

import org.bukkit.command.CommandSender;

public record CommandContext(CommandSender sender, String[] args) {}
