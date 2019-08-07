# PlaylistWriter

A small tool that:
- Scans a directory for all specified file extensions,
- Shuffles them,
- Breaks them down into smaller playlists, and
- Writes them to M3U8 playlists.

## Getting Started

- Copy config.json to a directory of your choice.
- Change "music folder" to the path you want to create a playlist from.
- Change "output folder" to the path you want the playlist[s] to be written to.

- To run, add a new debug configuration.
- "Main class" is za.co.k0ma.playlistwriter.MainKt.
- "program" arguments should contain the path to your config folder.
