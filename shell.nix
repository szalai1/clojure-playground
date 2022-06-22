with import <nixpkgs> {};
mkShell {
    buildInputs = [
    clojure
    nodejs
    leiningen
    ];
}
