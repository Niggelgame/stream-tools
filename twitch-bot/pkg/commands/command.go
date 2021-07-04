package commands

import "github.com/gempir/go-twitch-irc/v2"

type TwitchCommand struct {
	Name        string
	Description string
	Usage       string
	Options     map[string]string
	Execute     func(bot twitch.Client, channel string, args []string, commands map[string]*TwitchCommand)
}
