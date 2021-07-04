package commands

import (
	"github.com/gempir/go-twitch-irc/v2"
)

func NewPingCommand() *TwitchCommand {
	return &TwitchCommand{
		Name:        "ping",
		Description: "Pings the bot.",
		Usage:       "ping",
		Execute:     RunPing,
	}
}

func RunPing(client twitch.Client, channel string, args []string, _ map[string]*TwitchCommand) {
	client.Say(channel, "Pong!")
}
