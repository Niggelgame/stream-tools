package commands

import "github.com/gempir/go-twitch-irc/v2"

func NewHearkyDcCommand() *TwitchCommand {
	return &TwitchCommand{
		Name:        "hearkydc",
		Description: "provides my dc for hearky",
		Usage:       "hearky",
		Execute:     RunHearky,
	}
}

func RunHearky(client twitch.Client, channel string, args []string, _ map[string]*TwitchCommand) {
	client.Say(channel, "@hearky 's discord: https://discord.gg/sFW2QMtn3d")
}
