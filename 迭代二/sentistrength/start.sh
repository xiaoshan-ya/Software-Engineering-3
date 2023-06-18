#!/bin/bash
version=$(cat './pom.xml'  | grep '<version>'   |awk 'NR==1{print}' | awk -F '>' '{print $2}' | awk -F '<' '{print $1}'  )
url="https://git.nju.edu.cn/api/v4/projects/8331/packages/generic/my_package/"${version}"/SentiStrength.jar"
dataInfo='{ "name": "release", "tag_name": "'${version}'", "description": "2.0", "assets": { "links": [{ "name": "sentiStrength", "url": "https://git.nju.edu.cn/api/v4/projects/8331/packages/generic/my_package/'${version}'/SentiStrength.jar", "direct_asset_path": "/binaries/arm64", "link_type":"other" }] } }'
function release()
{
  cd /var/lib/jenkins/workspace/test
  curl --header "PRIVATE-TOKEN: glpat-vHkkwBeo8wbELj6yW3sr" \
       --upload-file target/SentiStrength.jar \
      "https://git.nju.edu.cn/api/v4/projects/8331/packages/generic/my_package/"${version}"/SentiStrength.jar"

  curl --header 'Content-Type: application/json' --header "PRIVATE-TOKEN: glpat-vHkkwBeo8wbELj6yW3sr" \
       --data '{ "name": "release",  "ref" : "master", "tag_name": "v'${version}'", "description": "2.0", "assets": { "links": [{ "name": "sentiStrength", "url": "https://git.nju.edu.cn/api/v4/projects/8331/packages/generic/my_package/'${version}'/SentiStrength.jar", "direct_asset_path": "/binaries/arm64", "link_type":"other" }] } }' \
       --request POST "https://git.nju.edu.cn/api/v4/projects/8331/releases"

}

release
