package main

import (
	"log"

	"github.com/gempir/go-twitch-irc/v2"
	"github.com/niggelgame/twitch-bot/pkg/bot"
	"github.com/niggelgame/twitch-bot/pkg/cfg"
	"github.com/niggelgame/twitch-bot/pkg/logger"
)

func main() {
	config := cfg.LoadConfig()

	log.Println("Starting bot...", config.Dev)

	logger.Initialize(config.Dev)

	client := twitch.NewClient(config.TwitchUsername, config.TwitchOAuth)

	bot.NewBot(client, config.TwitchChannel, config.Prefix).Run()

}
