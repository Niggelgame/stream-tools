package bot

import (
	"strings"

	"github.com/gempir/go-twitch-irc/v2"
	"github.com/niggelgame/twitch-bot/pkg/commands"
	"github.com/niggelgame/twitch-bot/pkg/events"
	"go.uber.org/zap"
)

func (bot *Bot) OnMessage(message twitch.PrivateMessage) {
	zap.L().Info("Private message from", zap.String("username", message.User.Name), zap.Any("Message", message.Message))

	msg := strings.Trim(strings.ToLower(message.Message), " ")
	args := strings.Split(msg, " ")
	if len(args) < 1 {
		return
	}

	cmd := args[0]

	if strings.HasPrefix(cmd, bot.Prefix) {
		zap.L().Debug("Has Prefix")
		cmd = strings.TrimPrefix(cmd, bot.Prefix)
		twCmd := bot.Commands[cmd]
		if twCmd == nil {
			zap.L().Debug("Command not found", zap.String("command", cmd))
			// bot.client.Say(bot.channel, "Command `"+cmd+"` not found")
			return
		}

		zap.L().Info("Executing command", zap.String("command", cmd), zap.Any("args", args[1:]), zap.Any("command", twCmd))

		twCmd.Execute(*bot.Client, message.Channel, args[1:], bot.Commands)
	}
}

func (bot *Bot) OnJoinMessage(message twitch.UserJoinMessage) {
	// TODO: Further testing pls
	// bot.client.Say(bot.channel, "Hello, "+message.User)
}

func (bot *Bot) OnWhisper(message twitch.WhisperMessage) {
	zap.L().Info("Whisper message from", zap.String("username", message.User.Name), zap.Any("Message", message.Message))
}

func (bot *Bot) Run() {
	bot.Client.Join(bot.Channel)

	bot.Client.OnConnect(func() {
		zap.L().Info("Connected to twitch")
	})

	bot.Client.OnWhisperMessage(bot.OnWhisper)
	bot.Client.OnPrivateMessage(bot.OnMessage)

	bot.Client.OnUserJoinMessage(bot.OnJoinMessage)

	bot.Client.OnNoticeMessage(func(message twitch.NoticeMessage) {
		events.Notice(message, bot.Client)
	})

	bot.Client.OnUserNoticeMessage(func(message twitch.UserNoticeMessage) {
		events.UserNotice(message, bot.Client)
	})

	zap.L().Info("Connecting to twitch")
	err := bot.Client.Connect()
	if err != nil {
		zap.L().Info("Could not connect to twitch: ", zap.Error(err))
		return
	}
}

func (bot *Bot) RegisterCommand(command *commands.TwitchCommand) {
	bot.Commands[command.Name] = command
}

func (bot *Bot) Stop() {
	zap.L().Info("Stopping bot")
	bot.Client.Disconnect()
}

func NewBot(client *twitch.Client, channel string, prefix string) *Bot {
	bot := &Bot{Client: client, Channel: channel, Prefix: prefix, Commands: make(map[string]*commands.TwitchCommand)}
	bot.RegisterCommand(commands.NewHelpCommand())
	bot.RegisterCommand(commands.NewPingCommand())
	bot.RegisterCommand(commands.NewTwitterCommand())
	bot.RegisterCommand(commands.NewHearkyDcCommand())

	return bot
}
