--- module (NICHT AENDERN!)
module LascaBot where
--- imports (NICHT AENDERN!)
import Util
import Data.Char
import System.Environment
--- external signatures (NICHT AENDERN!)
getMove   :: String -> String
listMoves :: String -> String

-- *==========================================* --
-- |    HIER BEGINNT EURE IMPLEMENTIERUNG!    | --
-- *==========================================* --

--- types/structures (TODO)

data Color = White | Black 
data FigureType = Soldier | Officer 


data Figure = Figure { figureType :: FigureType  
                                , color :: Color }
                            
data Point = Point { x:: Int, y::Int}

data Move = Move { from :: Point, to :: Point , isStrike :: Bool}
 
type Field = [Figure]
type Row = [Field]
type Board = [Row]

fieldSize :: Int 
fieldSize = 7

getMove   s 
  | not (listMoves s == "[]") = show (getRealMoves (getBoardFromInput s) (getColorFromInput s) !! 0)
  | otherwise = ""

listMoves s =  show (getRealMoves (getBoardFromInput s) (getColorFromInput s))
   
    
-- Field functions 

empty :: Field -> Bool
empty f = length f == 0

top :: Field -> Figure
top f = f !! 0
    
-- Board functions 

getRealMoves :: Board -> Color -> [Move]
getRealMoves b c 
          | canStrikeForColor b c  = possibleStrikesForColor b c 
          | otherwise = possibleMovesForColor b c



canStrikeForColor :: Board -> Color -> Bool
canStrikeForColor b c =  length (possibleStrikesForColor b c) > 0 

possibleStrikesForColor :: Board -> Color -> [Move]
possibleStrikesForColor b c = foldr (++) [] (map (\(o, dest) -> map (\d -> Move{from = o, to = d, isStrike = True}) dest ) (strikeWrongFormatForColor b c))

fieldAtCoordinate :: Board -> Point -> Field 
fieldAtCoordinate b p 
      | mod (y p) 2 == 0, mod (x p) 2 == 0 = fieldAt b (((findPos (x p) [2,4..6])!!0)) (y p)
      | mod (y p) 2 == 1, mod (x p) 2 == 1 = fieldAt b (((findPos (x p) [1,3..7])!!0)) (y p)
      | otherwise = []

fieldAt :: Board -> Int -> Int -> Field
fieldAt b x y  =  (b !! (y-1) ) !! (x)

getNotEmptyCoordinates :: Board -> [Point]
getNotEmptyCoordinates b = filter (\x -> not (empty (fieldAtCoordinate b x))) getBoardCoordinates

getCoordinatesForColor :: Board -> Color -> [Point]
getCoordinatesForColor b c = filter (\x -> color (top (fieldAtCoordinate b x)) == c) (getNotEmptyCoordinates b)


possibleMovesForColor :: Board -> Color -> [Move]
possibleMovesForColor b c = foldr  (++) [] (map (\w -> map (\x -> Move {from = w, to = x, isStrike = False }) (emptyReachablePoints b w)) (getCoordinatesForColor b c))

-- TODO check if the point next to the strike candidate is empty 

strikeWrongFormatForColor :: Board -> Color -> [(Point, [Point])]
strikeWrongFormatForColor n c = map (\(o, dest) -> (o, (map (\d -> (nextPoint o d)) dest ))) (filter (\(o, dest) -> length dest > 0) (map (\(o, dest) -> (o, filter (\d -> empty (fieldAtCoordinate n (nextPoint o d)) ) dest))(strikeCandidatesForColor n c)))

strikeCandidatesForColor :: Board -> Color -> [(Point, [Point])]
strikeCandidatesForColor b c = filter (\(o, d) -> length d > 0) (map (\p -> (p, (notEmptyReachablePointWithColor b p (oppColor c))))  (getCoordinatesForColor b c))

notEmptyReachablePointWithColor :: Board -> Point -> Color -> [Point]
notEmptyReachablePointWithColor b p c = filter (\x -> color (top (fieldAtCoordinate b x)) == c ) (notEmptyReachablePoint b p)

notEmptyReachablePoint :: Board -> Point -> [Point]
notEmptyReachablePoint b p = filter (\x -> not (empty (fieldAtCoordinate b x))) (reachablePoints b p)

emptyReachablePoints :: Board -> Point -> [Point]
emptyReachablePoints b p = filter (\x -> empty (fieldAtCoordinate b x)) (reachablePoints b p)

reachablePoints :: Board -> Point -> [Point]
reachablePoints b p = filter (\v ->  (x v < 8 && y v < 8 && x v > 0 && y v > 0)) (reachablePointsAll b p) 

reachablePointsAll :: Board -> Point -> [Point]
reachablePointsAll b p 
  | figureType (top (fieldAtCoordinate b p)) == Officer = [ addToPoint p 1 1 , addToPoint p (-1) 1, addToPoint p 1 (-1) , addToPoint p (-1) (-1)]
  | otherwise = [ addToPoint p 1 (movingDirForColor(color (top (fieldAtCoordinate b p)))) , addToPoint p (-1) (movingDirForColor (color (top (fieldAtCoordinate b p))))]
  
-- Color functions 

movingDirForColor :: Color -> Int 
movingDirForColor White = 1
movingDirForColor Black = -1

oppColor :: Color -> Color 
oppColor White = Black
oppColor Black = White 

-- Point functions 

createPoint :: Int -> Int -> Point
createPoint a b= Point {x = a, y = b}

getBoardCoordinates :: [Point]
getBoardCoordinates = foldr (++) [] (map (\y -> getRowCoordinates y) [1..fieldSize])

getRowCoordinates :: Int -> [Point]
getRowCoordinates y 
  | mod y 2 == 0 = map (\x -> createPoint x y) [2,4..6]
  | otherwise = map (\x -> createPoint x y) [1,3..7]

addToPoint :: Point -> Int -> Int -> Point 
addToPoint p xVar yVar = Point{x = ((x p) + xVar) , y = ((y p) + yVar)}  

nextPoint :: Point -> Point -> Point 
nextPoint a b 
  | ((x b)+((x b)-(x a)) < 8 && (y b)+((y b)-(y a)) < 8) && ((x b)+((x b)-(x a)) > 0 && (y b)+((y b)-(y a)) > 0) = Point{ x = (x b)+((x b)-(x a)) , y = (y b) + ((y b)-(y a))}
  | otherwise = b

--- Parser 

getBoardFromInput :: String -> Board
getBoardFromInput a = parseBoard ((splitOn " " a) !! 0)

getColorFromInput :: String -> Color
getColorFromInput a = parseColor ((splitOn " " a) !! 1)

parseBoard :: String -> Board 
parseBoard s = map parseRow (reverseStringArray (splitOn "/" s))

parseRow :: String -> Row 
parseRow s = map parseField (splitOn "," s)

parseField :: String -> Field
parseField s = map parseFigure (splitChars s)

parseFigure :: String -> Figure
parseFigure s = Figure{ figureType = (parseFigureType s), color = (parseColor s) }

parseColor :: String -> Color
parseColor "w" = White
parseColor "b" = Black
parseColor "W" = White
parseColor "B" = Black

parseFigureType :: String -> FigureType
parseFigureType "w" = Soldier
parseFigureType "b" = Soldier
parseFigureType "W" = Officer
parseFigureType "B" = Officer

-- Helper

findPos :: Eq a => a -> [a] -> [Int]
findPos elem = reverse . fst . foldl step ([],0) where
    step (is,i) e = (if e == elem then i:is else is, succ i) 

toInt :: Char -> Int
toInt c = ((ord c) - (ord 'a') + 1)

splitChars :: String -> [[Char]]
splitChars [] = []
splitChars s = [take 1 s] ++ splitChars (tail s)

reverseStringArray :: [String] -> [String]
reverseStringArray [] = []
reverseStringArray a = reverse a
    --- ... ---

--- output (TODO)

colorToString :: Color -> String
colorToString White = "w"
colorToString Black = "b"

figureToString :: Figure -> String 
figureToString (Figure{color = White, figureType = Soldier}) = "w"
figureToString (Figure{color = White, figureType = Officer}) = "W"
figureToString (Figure{color = Black, figureType = Soldier}) = "b"
figureToString (Figure{color = Black, figureType = Officer}) = "B"

intToChar :: Int -> String
intToChar 1 = "a"
intToChar 2 = "b"
intToChar 3 = "c"
intToChar 4 = "d"
intToChar 5 = "e"
intToChar 6 = "f"
intToChar 7 = "g"
intToChar _ = "out of bounds"

instance Show Figure where 
  show = figureToString 
  
instance Show Point where 
  show (Point{x = xVar, y = yVar}) = (intToChar xVar) ++ (show yVar)
  
instance Show Move where 
  show (Move{from = f, to = t}) = (show f) ++ "-" ++ (show t)
                                                       
instance Show Color where
    show = colorToString
    
instance Eq FigureType where
    (==) Officer Officer = True
    (==) Soldier Soldier = True
    (==) _ _ = False   
    
instance Eq Color where
    (==) White White = True
    (==) Black Black = True
    (==) _ _ = False    

    --- ... ---