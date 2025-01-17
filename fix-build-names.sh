fixBuildNames() {
  if ls *shaded* 1> /dev/null 2>&1; then
    fileName=$(find ./ -printf "%f\n" | grep "shaded")
    splitten=(${fileName//-shaded/ })
    splitten=(${fileName//-/ })

    module=${splitten[0]}
    version=${splitten[1]}

    mv BungeeUtilisalsX*.jar "original-BungeeUtilisalsX v$version-$module.jar"
    mv *-shaded.jar "BungeeUtilisalsX v$version-$module.jar"
    echo "Fixed build names for $module"
  fi
}

for moduleName in bungee velocity webapi; do
  if [[ $(pwd) == *target ]]; then
    cd ../../$moduleName/target
  else
    cd ./$moduleName/target
  fi
  fixBuildNames
done

echo "Successfully renamed BungeeUtilisalsX build names"
