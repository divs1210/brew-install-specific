# brew-install-specific

Install specific versions of brew packages.

## Installation

Download the latest release from [here](https://github.com/divs1210/brew-install-specific/releases).

## Usage

To find a package `pkg` with version `a.b.c`, run:
```
$ brew-install-specific pkg@a.b.c
```
This will list commits on the `pkg` homebrew formula that mention the given version along with their GitHub urls.
```
Matching versions:
1. pkg: update a.b.c bottle.
   https://github.com/Homebrew/homebrew-core/commit/<COMMIT-SHA>
2. pkg: release a.b.c-beta
   https://github.com/Homebrew/homebrew-core/commit/<COMMIT-SHA>
3. pkg a.b.c
   https://github.com/Homebrew/homebrew-core/commit/<COMMIT-SHA>

Select index: 
```
Verify the commit from the given URL, and enter the index of the selected commit.
```
Select index: 2
Run:
  brew install https://raw.githubusercontent.com/Homebrew/homebrew-core/<COMMIT-SHA>/Formula/pkg.rb
```
Copy and run the given command to install.

## License

Copyright © 2020 Divyansh Prakash

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.
