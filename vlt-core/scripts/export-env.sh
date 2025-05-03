#!/bin/sh

# TODO: Fix

# Source (`. ./scripts/export-env.sh`) to export every *.env
#  – root/.env
#  – builder/.env
#  – deployer/.env
#  – integrator/.env

uname_str="$(uname)"
root_dir="$(cd "$(dirname "$0")"/.. && pwd)"

load_env_file() {
  file="$1"
  [ -f "$file" ] || return

  # shellcheck disable=SC2046
  if [ "$uname_str" = "Linux" ]; then
    export $(grep -v '^#' "$file" | xargs -d '\n')
  else
    export $(grep -v '^#' "$file" | xargs -0)
  fi
}

load_env_file "$root_dir/.env"

for dir in "$root_dir"/*/ ; do
  load_env_file "${dir%.}/.env"
done