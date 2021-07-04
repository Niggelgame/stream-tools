package commands

import "github.com/gempir/go-twitch-irc/v2"

func NewHelpCommand() *TwitchCommand {
	return &TwitchCommand{
		Name:        "help",
		Description: "provide help about any command",
		Usage:       "help <command>",
		Execute:     RunHelp,
	}
}

func RunHelp(client twitch.Client, channel string, args []string, commands map[string]*TwitchCommand) {
	if len(args) == 0 {
		client.Say(channel, "Provide a command to get help with. ")
		return
	}

	commandName := args[0]
	command, ok := commands[commandName]
	if !ok {
		client.Say(channel, "There is no command called "+commandName+".")
		return
	}

	client.Say(channel, "Command: "+command.Name+" Usage: '"+command.Usage+"' Description: "+command.Description)
}
