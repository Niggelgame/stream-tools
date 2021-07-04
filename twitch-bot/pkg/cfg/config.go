package cfg

import (
	"log"

	"github.com/joho/godotenv"
	"github.com/kelseyhightower/envconfig"
)

type Config struct {
	Dev            bool   `default:"false"`
	TwitchUsername string `envconfig:"TWITCH_USERNAME"`
	TwitchOAuth    string `envconfig:"TWITCH_OAUTH"`
	TwitchChannel  string `envconfig:"TWITCH_CHANNEL"`
	Prefix         string `default:"!"`
}

func LoadConfig() *Config {
	_ = godotenv.Load()

	var cfg Config
	err := envconfig.Process("BOT", &cfg)
	if err != nil {
		log.Fatal("failed to laod config: ", err)
	}

	return &cfg
}
