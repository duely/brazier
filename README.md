# Alembic
Alembic provides a way for advancement-driven mods such as Patchouli (and the future release of HQM's Advancement Task) to interact with Thaumcraft's (and its addons) research. Advancements are generated automatically and per stage, following the pattern:

`alembic:category/research_key@research_stage`

For example:

`alembic:basics/firststeps@1` is is an advancement that is received when completing the first stage of the "First Steps" research (achieving a Thaumonomicon).

If you just wish for the entire research to be completed, you need the highest/final stage number, example: `alembic:basics@firststeps@3`

This mod is still in beta! Reload events (such as the /reload command for dedicated servers, and resource pack changes for integrated clients) should be handled automatically, but there may be issues. Please report any bugs to [GitHub](https://github.com/duely/dwmh/issues), or reach out on [my Discord](https://discordapp.com/invite/5QwFfNb).

# Commands
`/research <term>`

This command will search research entry keys to find a matching word. It will then display the name of the research (localised), the names of all the relevant advancements, which stages have been completed (in research) and advances (which should be synchronised). Example: 

![](https://i.imgur.com/fwLPyY3.png)