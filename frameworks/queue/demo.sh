#!/bin/bash

SERVICE_NAME=${SERVICE_NAME:-queue}

read -r -d '' SPEC_TEMPLATE <<EOF
name: SVCNAME
pods:
  node:
    count: 2
    tasks:
      task:
        goal: RUNNING
        cmd: "echo this is SVCNAME >> output && sleep 1000"
        cpus: 0.1
        memory: 252
EOF

runCurl() {
    echo "Slow CLI..."
    TOKEN="Authorization: token=$(dcos config show core.dcos_acs_token)"
    URL=$(dcos config show core.dcos_url)/service/${SERVICE_NAME}${2}
    echo "$1 $2 ..." 1>&2
    if [ -n "$3" ]; then
        # "@-" = read from stdin
        (set -o xtrace; echo "$3" | curl -F 'file=@-' -F 'type=yaml' -X $1 -H "$TOKEN" $URL)
    else
        (set -o xtrace; curl -X $1 -H "$TOKEN" $URL)
    fi
    echo
}

syntax() {
    echo "Commands:"
    echo "  list"
    echo "  <svcname> add"
    echo "  <svcname> remove"
    echo ""
    echo "  <svcname> plans"
    echo "  <svcname> deployplan"
    echo "  <svcname> recoveryplan"
    echo "  <svcname> pods"
    echo "  <svcname> restart"
    echo "  <svcname> replace"
    echo "  <svcname> endpoints"
    exit 1
}

if [ -z "$1" ]; then
    syntax
fi

svcname=$1
cmd=$2

# special case: list command doesn't need svcname
if [ "$svcname" == "list" ]; then
    #runCurl GET /v1/runs
    dcos queue run list
    exit
fi

# all other commands require svcname:
case $cmd in
    add)
        SPEC=$(echo "$SPEC_TEMPLATE" | sed s/SVCNAME/${svcname}/g)
        #runCurl POST /v1/runs "$SPEC"
        echo "$SPEC" | dcos queue run add yaml stdin
        ;;
    remove)
        #runCurl DELETE /v1/runs/${svcname}
        dcos queue run remove ${svcname}
        ;;

    plans)
        #runCurl GET /v1/run/${svcname}/plans
        dcos queue --run=${svcname} run plan list
        ;;
    deployplan)
        #runCurl GET /v1/run/${svcname}/plans/deploy
        dcos queue --run=${svcname} run plan status deploy
        ;;
    recoveryplan)
        #runCurl GET /v1/run/${svcname}/plans/recovery
        dcos queue --run=${svcname} run plan status recovery
        ;;

    pods)
        #runCurl GET /v1/run/${svcname}/pod
        dcos queue --run=${svcname} run pod list
        ;;
    restart)
        #runCurl POST /v1/run/${svcname}/pod/node-0/restart
        dcos queue --run=${svcname} run pod restart node-0
        ;;
    replace)
        #runCurl POST /v1/run/${svcname}/pod/node-0/replace
        dcos queue --run=${svcname} run pod replace node-0
        ;;

    endpoints)
        #runCurl POST /v1/run/${svcname}/endpoints
        dcos queue --run=${svcname} run endpoints
        ;;
    *)
        syntax
        ;;
esac

