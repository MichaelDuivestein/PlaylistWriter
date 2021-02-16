# PlaylistWriter

A small tool that:
- Scans a directory for all specified file extensions,
- Shuffles them,
- Breaks them down into smaller playlists, and
- Writes them to M3U8 playlists.

## Getting Started

- Open the src/resources/config.json file.
- Change "music folder" to the path you want to create a playlist from.
- Change "output folder" to the path you want the playlist[s] to be written to.
- Change any other settings as you see fit.

- To run, add a new debug configuration.
- "Main class" is za.co.k0ma.playlistwriter.MainKt.
- "program" arguments should contain the path to your config folder.
