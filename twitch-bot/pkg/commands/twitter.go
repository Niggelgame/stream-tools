package commands

import "github.com/gempir/go-twitch-irc/v2"

func NewTwitterCommand() *TwitchCommand {
	return &TwitchCommand{
		Name:        "twitter",
		Description: "provides my twitter account",
		Usage:       "twitter",
		Execute:     RunTwitter,
	}
}

func RunTwitter(client twitch.Client, channel string, args []string, _ map[string]*TwitchCommand) {
	client.Say(channel, "@niggelgame 's twitter: https://twitter.com/niggelgame")
}
