function [mems, pj, cij] = frecca(similarities, n_clusters, varargin)
%
%   Purpose: 
%   -------
%   Perform clustering using the FRECCA algorithm.
%
%   Inputs:  
%   -------
%   SIMILARITIES (required): 
%    - square matrix of pairwise similarities between objects
%   N_CLUSTERS (required): 
%    - number of clusters to find
%   ALPHA (optional): 
%    - controls the random jump in Eigenvector centrality calculation (default 0.85)
%   EPSILON (optional): 
%    - minimum quadratic error in Eigenvector centrality calculation (default 1e-6)
%   MAX_LOOP_ITERS (optional): 
%    - maximum number of EM loop iterations to perform (default 500)
%   EQUAL_CLUSTERS_BIAS (optional): 
%    - Bias towards equal-sized clusters (0 for no bias; 1 for full bias) (default 0.5)
%
%   Optional parameters are called as name-value pairs; e.g., 
%       >> frecca(similarities, n_clusters, 'max_loop_iters', 100, 'alpha', 0.7)
%
%   Outputs:  
%   --------
%   MEMS: 
%    - n by k matrix of cluster membership values where entry (n,k) represents the 
%      probability of membership of object n to cluster k (rows sum to 1).
%   PJ: 
%    - n by k matrix of cluster centrality values where entry (n,k) represents the 
%      centrality of object n in cluster k (columns sum to 1).
%   CIJ: 
%    - 1 by k matrix, where the kth element represents the proportion of objects belonging to class k
%
%   Author:
%   -------
%   Andrew Skabar  (a.skabar@latrobe.edu.au)
%
%   Reference:
%   ----------
%   Skabar, A. and Abdalgader, K. (2013) "Clustering sentence-level text using a 
%   novel fuzzy relational clustering algorithm". IEEE Transactions on 
%   Knowledge and Data Engineering, 25(1), 62-75.
%
%   ---------------------------------------------------------------------------

% Check that input matrix is square 
[r c] = size(similarities);
if (r ~= c)
    error('Input matrix must be square.');
end

% Set defaults
alpha = 0.85;
epsilon = 1e-6;
max_loop_iters = 500;
equal_clusters_bias = 0.5;

% Override defaults if a name-value pair has been entered
while ~isempty(varargin)
    switch upper(varargin{1})
        case 'ALPHA'
            alpha = varargin{2};
            varargin(1:2) = [];
        case 'EPSILON'
            epsilon = varargin{2};
            varargin(1:2) = [];
        case 'MAX_LOOP_ITERS'
            max_loop_iters = varargin{2};
            varargin(1:2) = [];
        case 'EQUAL_CLUSTERS_BIAS'
            equal_clusters_bias = varargin{2};
            varargin(1:2) = [];
        otherwise
            error(['Unexpected option: ' varargin{1}])
    end
end
   
N = size(similarities, 1);   % size of similarity matrix

% Normalize similarity matrix so rows and columns sum to approximately 1
D = sum(similarities, 2) + (1e-10);
Dsqt = full(spdiags(sqrt(1./D), 0, N, N));
similarities = Dsqt * similarities * Dsqt;

% Storage for mixture probabilities 
cij = zeros(N, n_clusters);                 % eig centrality of object i 
                                            % in cluster j
pj = ones(1, n_clusters)/n_clusters;        % cluster priors
pj_equal = ones(1, n_clusters)/n_clusters;  % equal cluster priors 
                                            % (used when biasing towards 
                                            % equal-sized clusters - see below)
mems = zeros(N, n_clusters);                % cluster membership values

% Initialise membership values to small random values about equal prior
mems = randn(N, n_clusters)/1000 + 1.0/n_clusters;
mems = mems./repmat(sum(mems,2), 1, n_clusters);  % row normalize

%%% EXPECTATION MAXIMIZATION loop
residual = inf;
k = 0;
while (residual >= 1e-5) && (k <= max_loop_iters)
    %%% --Expectation step--
    for j = 1 : n_clusters  
        % extract membership values for col j
        vals = mems(:,j);
        % scale similarities by cluster membership values
        H = similarities .* repmat(vals, 1, N) .* repmat(vals', N, 1);
        % scale H so that sum of rows and columns is not too high or low -
        % ensures that alpha value has comparable effect over different
        % clusters
        H = H./repmat(max(sum(H, 1)), N, N);  
        % calculate and store eigenvector centrality value
        cij(:,j) = EV_Centrality(H, alpha, epsilon); 
    end

    %%% --Maximization step--
    % Calculate new membership values
    tmp = cij .* repmat(pj, N, 1);
    sum2tmp = sum(tmp,2);
    new_mems = tmp ./ repmat(sum2tmp, 1, n_clusters);
    % Calculate new cluster priors
    pj = pj_equal + (sum(new_mems,1)/N - pj_equal) * (1.0 - equal_clusters_bias);
    % Loop control
    residual = norm(new_mems - mems,'fro');
    k = k + 1;
    mems = new_mems;    
end


function [v] = EV_Centrality(M, d, v_quadratic_error)
% EC_CENTRALITY  Calculates principal eigenvector of matrix M
N = size(M,1); 
v = rand(N,1);
v = v ./ norm(v);
last_v = ones(N,1) * inf;
while(norm(v - last_v) > v_quadratic_error)
        last_v = v;
        v = (d .* (M * v)) + ((1 - d) / N) .* ones(N,1);
        v = v ./ norm(v);
end
% Normalize principal eigenvector so components sum to 1.
v = v / norm(v,1);

