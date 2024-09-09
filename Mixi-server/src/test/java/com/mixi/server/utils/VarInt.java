class Solution {

    public boolean[][] check;
    public int[] dx = {0, 0, -1, 1};
    public int[] dy = {-1, 1, 0, 0};
    public int ret, m, n, dir;
    /* Write Code Here */
    public int numberOfPatrolBlocks(int[][] block) {
        ret = 0;
        m = block.length;
        n = block[0].length;
        check = new boolean[m][n];
        dfs(block, 0, 0);
        return ret;
    }
    public void dfs(int[][] block, int i, int j) {
        // ret++;
        // check[i][j] = true;
        // for (int k = 0; k < 4; k++) {
        //     int x = dx[k] + i, y = dy[k] + j;
        //     if (x >= 0 && x < m && y >=0 && y < n && !check[x][y] && block[x][y] != 1) {
        //         dfs(block, x, y);
        //     }
        // }
        ret++;
        check[i][j] = true;
        while (true) {
            int x = i + dx[dir];
            int y = j + dy[dir];
            if (x >= 0 && x < m && y >= 0 && y < n && !check[x][y] && block[x][y] != 1) {
                dfs(block, x, y);
                return;
            } else {
                dir = (dir + 1) % 4;
                x = i + dx[dir];
                y = j + dy[dir];
                if (x >= 0 && x < m && y >= 0 && y < n && !check[x][y] && block[x][y] != 1) {
                    dfs(block, x, y);
                    return;
                }
            }
            if (dir == 0) {
                break;
            }
        }
    }
}
