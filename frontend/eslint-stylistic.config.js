import stylistic from '@stylistic/eslint-plugin';

const plugins = { '@stylistic': stylistic }

const rules = {
    ...stylistic.configs['recommended-flat'].rules,

    '@stylistic/brace-style': ['error', '1tbs'],
    '@stylistic/operator-linebreak': ['error', 'before', { 'overrides': { '=': 'after' }}],
    '@stylistic/array-bracket-spacing': ['error', 'never'],
    '@stylistic/arrow-parens': ['error', 'as-needed'],
    '@stylistic/comma-dangle': ['error', 'always-multiline'],
    '@stylistic/eol-last': 'error',
    '@stylistic/keyword-spacing': 'error',
    '@stylistic/member-delimiter-style': 'error',
    '@stylistic/new-parens': 'error',
    '@stylistic/no-multiple-empty-lines': 'error',
    '@stylistic/no-trailing-spaces': 'error',
    '@stylistic/object-curly-spacing': ['error', 'always'],
    '@stylistic/quote-props': ['error', 'consistent-as-needed'],
    '@stylistic/quotes': ['error', 'single', { avoidEscape: true }],
    '@stylistic/semi': ['error', 'always'],
    '@stylistic/type-annotation-spacing': 'error',
    '@stylistic/array-element-newline': ['error', { 'consistent': true, 'multiline': true }],
    '@stylistic/space-before-function-paren': [
        'error',
        {
            anonymous: 'never',
            asyncArrow: 'always',
            named: 'never',
        },
    ],
    '@stylistic/max-len': [
        'error',
        {
            ignorePattern: '^import |^export \\{(.*?)\\}',
            code: 140,
        },
    ],
    '@stylistic/indent': [
        'error',
        2,
        {
            MemberExpression: 1,
            ignoredNodes: [
                'FunctionExpression > .params[decorators.length > 0]',
                'FunctionExpression > .params > :matches(Decorator, :not(:first-child))',
                'ClassBody.body > PropertyDefinition[decorators.length > 0] > .key',
            ],
            SwitchCase: 1,
        },
    ],
}

const config = {
    plugins,
    rules,
}

export default config;