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
data FigureType = Soldier | Officer deriving (Show)


data Figure = Figure { figureType :: FigureType  
                                , color :: Color
                            } 

type Field = [Figure]
                           
empty :: Field -> Bool
empty f = length f == 0
             

    --- ... ---

--- logic (TODO)
getMove   s = "g3-f4" -- Eigene Definition einfügen!
listMoves s = "[g3-f4,...]" -- Eigene Definition einfügen!

    --- ... ---

--- input (TODO)

parseField :: String -> Field
parseField s = map parseFigure (splitChars s)

--parseInput :: [String] -> ...
--parseInput (board:color:[])   = ... (parseColor color) ...
--parseInput (board:color:move:[]) = ... (parseColor color) ...


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


toInt :: Char -> Int
toInt c = ((ord c) - (ord 'a') + 1)

splitChars :: String -> [[Char]]
splitChars [] = []
splitChars s = [take 1 s] ++ splitChars (tail s)



    --- ... ---

--- output (TODO)

colorToString :: Color -> String
colorToString White = "w"
colorToString Black = "b"

instance Show Figure where 
  show (Figure{color = c, figureType = f})= show f ++ "," ++ show c

instance Show Color where
    show = colorToString
    
instance Eq Color where
    (==) White White = True
    (==) Black Black = True
    (==) _ _ = False    

    --- ... ---