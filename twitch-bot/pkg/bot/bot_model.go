package bot

import (
	"github.com/gempir/go-twitch-irc/v2"
	"github.com/niggelgame/twitch-bot/pkg/commands"
)

type Bot struct {
	Client   *twitch.Client
	Prefix   string
	Channel  string
	Commands map[string]*commands.TwitchCommand
}
